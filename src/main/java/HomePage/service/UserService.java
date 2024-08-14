package HomePage.service;

import HomePage.controller.UserForm;
import HomePage.domain.model.User;
import HomePage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long join(User user){
        validateDuplicateMember(user);
        userRepository.save(user);
        return user.getId();
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
