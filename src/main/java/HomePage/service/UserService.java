package HomePage.service;

import HomePage.controller.UserForm;
import HomePage.domain.model.User;
import HomePage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class UserService {
    private final UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private static final List<String> VALID_ROLES = Arrays.asList("ROLE_USER", "ROLE_ADMIN");
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        // 추가적인 필드 검증 로직을 여기에 구현할 수 있습니다.
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
}
