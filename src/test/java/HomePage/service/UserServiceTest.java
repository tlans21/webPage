package HomePage.service;

import HomePage.domain.model.User;
import HomePage.repository.MemoryUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {
    UserService userService;
    MemoryUserRepository memberRepository;
    @BeforeEach
    public void beforeEach() {
        memberRepository = new MemoryUserRepository();
        userService = new UserService(memberRepository);
    }
    @AfterEach
    public void afterEach() {
        memberRepository.clearStore();
    }

    @Test
    void join() {
        //given
        User user = new User();
        user.setUsername("simun");
        //when
        Long saveId = userService.join(user);

        //then
        User findUser = userService.findOne(saveId).get();
        assertThat(user.getUsername()).isEqualTo(findUser.getUsername());

    }

    @Test
    void joinException() {
        //given
        User user1 = new User();

        user1.setUsername("simun");
        user1.setEmail("tlans20@naver.com");
        User user2 = new User();
        user2.setUsername("simun");
        user2.setEmail("tlans21@naver.com");
        //when

        User user3 = new User();
        user3.setUsername("simun100");
        user3.setEmail("tlans22@naver.com");

        User user4 = new User();
        user4.setUsername("simun200");
        user4.setEmail("tlans22@naver.com");

        userService.join(user1);
        IllegalStateException e1 = assertThrows(IllegalStateException.class, () -> userService.join(user2));
        assertThat(e1.getMessage()).isEqualTo("이미 존재하는 이름입니다.");

        userService.join(user3);
        IllegalStateException e2 = assertThrows(IllegalStateException.class, () -> userService.join(user4));
        assertThat(e2.getMessage()).isEqualTo("이미 존재하는 이메일입니다.");
        //then
    }

    @Test
    void findOne() {
        User user = new User();
        user.setUsername("simun");
        userService.join(user);
        User user1 = userService.findOne(user.getId()).get();
        assertThat(user.getUsername()).isEqualTo(user1.getUsername());
    }
    @Test
    public void 로그인(){
        User registeredUser = new User();
        registeredUser.setUsername("simunss");
        registeredUser.setEmail("simun@naver.com");
        registeredUser.setPassword("1234");
        userService.join(registeredUser);

        User user = new User();
        user.setUsername("simunss");
        user.setEmail("simun@naver.com");
        user.setPassword("1234");
        User result = userService.authenticateMember(user.getEmail(), user.getPassword()).get();
        assertThat(user.getUsername()).isEqualTo(result.getUsername());
    }
}