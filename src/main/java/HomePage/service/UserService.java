package HomePage.service;

import HomePage.controller.UserForm;
import HomePage.domain.model.Page;
import HomePage.domain.model.User;
import HomePage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class UserService {
    @Value("${communityBoard.page-size}")
    private int pageSize = 10;

    private final UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private static final List<String> VALID_ROLES = Arrays.asList("ROLE_USER", "ROLE_ADMIN");
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Long join(User user){
        validateUserFields(user);
        validateEmailFormat(user.getEmail());
        validateRole(user.getRoles());
        validateDuplicateMember(user);
        userRepository.save(user);
        return user.getId();
    }
    private void validateUserFields(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("이름이 필요합니다.");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("이메일이 필요합니다.");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호가 필요합니다.");
        }
    }
    private void validateEmailFormat(String email) {
       if (!isValidEmail(email)) {
           throw new IllegalArgumentException("부정확한 이메일 포멧입니다.");
       }
   }

   private boolean isValidEmail(String email) {
       return email != null && EMAIL_PATTERN.matcher(email).matches();
   }

    public void validateDuplicateMember(User user) {
        userRepository.findByUsername(user.getUsername())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 이름입니다.");
                });

        userRepository.findByEmail(user.getEmail())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 이메일입니다.");
                });
    }
    private void validateRole(String role) {
       if (!VALID_ROLES.contains(role)) {
           throw new IllegalArgumentException("타당하지않은 권한입니다.");
       }
   }


    public User userFormDTO(UserForm userForm) {
        User user = User.builder()
                .email(userForm.getEmail())
                .password(bCryptPasswordEncoder.encode(userForm.getPassword()))
                .username(userForm.getUsername())
                .role("ROLE_USER")
                .build();
        return user;
    }


    public List<User> findMembers(){
        return userRepository.findAll();
    }

    public Optional<User> findOne(Long memberId){
        return userRepository.findById(memberId);
    }

    public Optional<User> findByUsername(String username){
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()){
            return Optional.empty();
        }
        return user.stream().findAny();
    }
    public Optional<User> authenticateMember(String username, String rawPassword){
        Optional<User> user = userRepository.findByUsername(username);
        if(!user.isPresent()){
            System.out.println("이름으로 찾을수없음");
            return Optional.empty();
        }
        if(!bCryptPasswordEncoder.matches(rawPassword, user.get().getPassword())){
            System.out.println("패스워드 동일하지않음");
            return Optional.empty();
        }

        return user.stream().findAny();
    }

    public Page<User> getUsersPage(int pageNumber){
        if (pageNumber <= 0) {
            throw new IllegalArgumentException("Page number must be greater than 0");
        }

        int totalUsers = userRepository.count();
        int totalUserPages = (int) Math.ceil((double) totalUsers / pageSize);

        if (pageNumber > totalUserPages) {
            pageNumber = totalUserPages; // or throw an exception if you prefer
        }

        int offset = (pageNumber - 1) * pageSize;
        List<User> users = userRepository.findUserPage(offset, pageSize);

        return new Page<User>(users, pageNumber, totalUserPages, pageSize);
    }
    public Page<User> getUsersPageBySearch(int pageNumber, String searchType, String searchKeyword){
        List<User> users;
        int offset = (pageNumber - 1) * pageSize;
        int totalUsers;
        int totalUserPages;

        switch (searchType) {
            case "id" -> {
                if (isValidIdInput(searchKeyword)) {
                    try {
                        Long idKeyword = Long.parseLong(searchKeyword);
                        totalUsers = userRepository.countById(idKeyword);
                        users = userRepository.findUserPageById(offset, pageSize, idKeyword);

                    } catch (NumberFormatException e) {
                        // Long으로 담을수 없는 크기의 ID값이 올 시에 발생
                        return getUsersPage(pageNumber);
                    }
                } else {
                    return getUsersPage(pageNumber);
                }
            }
            case "username" -> {
                if (isSearchByUsername(searchKeyword)) {
                    totalUsers = userRepository.countByUsername(searchKeyword);
                    users = userRepository.findUserPageByUsername(offset, pageSize, searchKeyword);
                } else {
                    return getUsersPage(pageNumber);
                }
            }
            case "email" -> {
                if (isSearchByEmail(searchKeyword)) {
                    totalUsers = userRepository.countByEmail(searchKeyword);
                    users = userRepository.findUserPageByEmail(offset, pageSize, searchKeyword);
                } else {
                    return getUsersPage(pageNumber);
                }
            }
            case "role" -> {
                if (isSearchByRole(searchKeyword)) {
                    totalUsers = userRepository.countByRole(searchKeyword);
                    users = userRepository.findUserPageByRole(offset, pageSize, searchKeyword);
                } else {
                    return getUsersPage(pageNumber);
                }
            }
            default -> {
                return getUsersPage(pageNumber);
            }
        }

        totalUserPages = Math.max(1, (int) Math.ceil((double) totalUsers / pageSize));
        return new Page<User>(users, pageNumber, totalUserPages, pageSize);
    }

    private boolean isSearchByUsername(String writer){
        return !isNullOrEmpty(writer);
    }
    private boolean isSearchByEmail(String username){
        return !isNullOrEmpty(username);
    }
    private boolean isSearchByRole(String role){
        return !isNullOrEmpty(role);
    }
    private boolean isNullOrEmpty(String str){
        return str == null || str.trim().isEmpty();
    }
    private boolean isValidIdInput(String input){
        return input != null && input.matches("\\d+");
    }
}
