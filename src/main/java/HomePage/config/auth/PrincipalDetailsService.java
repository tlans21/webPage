package HomePage.config.auth;

import HomePage.domain.model.User;
import HomePage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

// 시큐리티 설정 loginProcessingUrl("/login");
// login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC되어 있는 loadUsernameByUsername 함수가 실행됨.
@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;


    // 시큐리티 session(내부 Authentication(내부 UserDetails))
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        System.out.println("LoadUserByName : " + username);
        if (userOptional.isPresent()){
            User userEntity = userOptional.get();
            System.out.println("A");
            return new PrincipalDetails(userEntity);
        }
        System.out.println("B");
        return null;
    }

}
