package HomePage.service;

import HomePage.domain.model.User;
import HomePage.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long join(User user){
        validateDuplicateMember(user);
        userRepository.save(user);
        return user.getId();
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

    public Optional<User> authenticateMember(String email, String password){
        Optional<User> member = userRepository.findByEmail(email);
        System.out.println(member.get());
        System.out.println(member.get().getEmail());
        System.out.println(member.get().getPassword());
        if(!member.isPresent()){
            return Optional.empty();
        }
        System.out.println(password);
        if(!password.equals(member.get().getPassword())){
            return Optional.empty();
        }

        return member.stream().findAny();
    }
}
