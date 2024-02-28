package HomePage.service;

import HomePage.domain.model.User;
import HomePage.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional

class UserServiceIntegrationTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    @Test
    public void 회원가입() throws Exception {
        //Given
        User user = new User();
        user.setUsername("hello");
        //When
        Long saveId = userService.join(user);
        //Then
        User findUser = userRepository.findById(saveId).get();
        assertEquals(user.getUsername(), findUser.getUsername());
    }
    @Test
    public void 중복_회원_예외() throws Exception {
        //Given
        // 이름만 중복인 경우
        User user1 = new User();
        user1.setUsername("spring");
        user1.setEmail("tlans20@naver.com");
        User user2 = new User();
        user2.setUsername("spring");
        user2.setEmail("tlans21@naver.com");

        // 이메일이 중복인 경우
        User user3 = new User();
        user3.setUsername("simun1");
        user3.setEmail("tlans21@naver.com");
        User user4 = new User();
        user4.setUsername("simun2");
        user4.setEmail("tlans21@naver.com");

        //When
        userService.join(user1);
        IllegalStateException e1 = assertThrows(IllegalStateException.class,
                () -> userService.join(user2));//예외가 발생해야 한다.
        assertThat(e1.getMessage()).isEqualTo("이미 존재하는 이름입니다.");
        userService.join(user3);
        IllegalStateException e2 = assertThrows(IllegalStateException.class,
                () -> userService.join(user4));//예외가 발생해야 한다.
        assertThat(e2.getMessage()).isEqualTo("이미 존재하는 이메일입니다.");
    }

    @Test
    public void 로그인(){
        User registeredUser = new User();
        registeredUser.setUsername("simunss");
        registeredUser.setEmail("simun@naver.com");
        registeredUser.setPassword("1234");
        userService.join(registeredUser); // 회원 가입

        User user = new User();
        user.setUsername("simunss");
        user.setEmail("simun@naver.com");
        user.setPassword("1234");
        Optional<User> result = userService.authenticateMember(user.getEmail(), user.getPassword());
        assertThat(user.getUsername()).isEqualTo(result.get().getUsername());
    }
}