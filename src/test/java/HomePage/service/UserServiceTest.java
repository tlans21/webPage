package HomePage.service;

import HomePage.controller.UserForm;
import HomePage.domain.model.User;
import HomePage.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    @BeforeEach
    void setUp() {
        // 테스트 시작 전 테이블 비우기
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        userRepository.setTableName("test.user");
        cleanUpDatabase();
    }

    @AfterEach
    void tearDown(){
        // 테스트 시작 후 테이블 비우기
        cleanUpDatabase();
    }
    private void cleanUpDatabase() {
        jdbcTemplate.execute("SET SQL_SAFE_UPDATES = 0");
        try {
            jdbcTemplate.execute("DELETE FROM test.user where id > 1");
        } finally {
            jdbcTemplate.execute("SET SQL_SAFE_UPDATES = 1");
        }
    }

    @Test
    void join() {
        //given : 준비
        User user = new User();
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setEmail("test@test.com");
        user.setRoles("ROLE_USER");
        //when : 실행
        Long joinedUserId = userService.join(user);
        //then : 검증
        User foundUser = userService.findOne(joinedUserId).orElse(null);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("test");
        assertThat(foundUser.getEmail()).isEqualTo("test@test.com");
        assertThat(foundUser.getRoles()).isEqualTo("ROLE_USER");
        assertThat(foundUser.getPassword()).isNotEmpty();
    }
    @Test
    void joinWithMissingRequiredField() {
        //given : 준비
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("password");
        // when & then : 실행 및 검증
        assertThatThrownBy(() -> userService.join(user))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("이름이 필요합니다.");
    }

    @Test
    void joinWithInvalidEmail() {
        //given : 준비
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("invalid-email");
        user.setPassword("password");
        // when & then : 실행 및 검증
        assertThatThrownBy(() -> userService.join(user))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("부정확한 이메일 포멧입니다.");
    }
//    @Test
//    void joinWithWeakPassword() {
//        //given : 준비
//        User user = new User();
//        user.setUsername("testuser");
//        user.setEmail("test@test.com");
//        user.setPassword("weak");
//        //when :& then : 실행 및 검증
//        assertThatThrownBy(() -> userService.join(user))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("Password does not meet complexity requirements");
//    }

    @Test
    void joinWithValidEmail() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("valid.email@example.com");
        user.setPassword("password");
        user.setRoles("ROLE_USER");

        Long joinedUserId = userService.join(user);

        assertThat(joinedUserId).isNotNull();
        Optional<User> foundUser = userService.findOne(joinedUserId);
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("valid.email@example.com");
    }

    @Test
    void setInvalidRole() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setRoles("INVALID_ROLE");

        assertThatThrownBy(() -> userService.join(user))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("타당하지않은 권한입니다.");
    }


    @Test
    void userFormDto(){
        //given : 준비
        UserForm userForm = new UserForm();
        userForm.setUsername("test");
        userForm.setPassword("testPassword");
        userForm.setEmail("test@test.com");
        //when : 실행
        User user = userService.userFormDTO(userForm);
        //then : 검증
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("test");
        assertThat(user.getEmail()).isEqualTo("test@test.com");
        assertThat(passwordEncoder.matches("testPassword", user.getPassword())).isTrue();
    }

    @Test
    void validateDuplicateMember() {
        //given : 준비(Username 중복 상황)
        User user1 = new User();
        user1.setUsername("test1");
        user1.setEmail("test@test.com");
        user1.setPassword("testPassword");
        user1.setRoles("ROLE_USER");

        User user2 = new User(); // Username 중복
        user2.setUsername("test1");
        user2.setEmail("test2@test.com");
        user2.setPassword("testPassword");
        user2.setRoles("ROLE_USER");

        User user3 = new User(); // Email 중복
        user3.setUsername("test3");
        user3.setEmail("test@test.com");
        user3.setPassword("testPassword");
        user3.setRoles("ROLE_USER");

        userRepository.save(user1);
        //when & then : 실행 및 검증
        assertThatThrownBy(() -> userService.validateDuplicateMember(user2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 존재하는 이름입니다.");
        assertThatThrownBy(() -> userService.validateDuplicateMember(user3))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 존재하는 이메일입니다.");



    }

    @Test
    void findMembers() {
        //given: 준비
        User user1 = new User();
        user1.setUsername("test1");
        user1.setEmail("test1@test1.com");
        user1.setPassword("testPassword");
        user1.setRoles("ROLE_USER");

        User user2 = new User(); // Username 중복
        user2.setUsername("test2");
        user2.setEmail("test2@test2.com");
        user2.setPassword("testPassword");
        user2.setRoles("ROLE_USER");

        User user3 = new User(); // Email 중복
        user3.setUsername("test3");
        user3.setEmail("test3@test3.com");
        user3.setPassword("testPassword");
        user3.setRoles("ROLE_USER");

        userService.join(user1);
        userService.join(user2);
        userService.join(user3);

        //when : 실행
        List<User> users = userService.findMembers();
        //then : 검증
        assertThat(users).isNotNull();
        assertThat(users.size()).isEqualTo(3);

        List<String> expectedUsernames = Arrays.asList("test1", "test2", "test3");
        List<String> expectedEmails = Arrays.asList("test1@test1.com", "test2@test2.com", "test3@test3.com");

        for (User user: users){
            assertThat(user).isNotNull();
            assertThat(expectedUsernames).contains(user.getUsername());
            assertThat(expectedEmails).contains(user.getEmail());
            assertThat(user.getRoles()).isEqualTo("ROLE_USER");
        }   
    }

    @Test
    void findOne() {
        //given : 준비
        User user = new User();
        user.setEmail("test1@test1.com");
        user.setPassword("testPassword");
        user.setUsername("test1");
        user.setRoles("ROLE_USER");

        Long joinedUserId = userService.join(user);

        //when : 실행
        Optional<User> foundUser = userService.findOne(joinedUserId);
        //then : 검증

        assertThat(foundUser).isPresent();

        User retrievedUser = foundUser.get();

        assertThat(retrievedUser.getId()).isEqualTo(joinedUserId);
        assertThat(retrievedUser.getEmail()).isEqualTo("test1@test1.com");
        assertThat(retrievedUser.getUsername()).isEqualTo("test1");
        assertThat(retrievedUser.getRoles()).isEqualTo("ROLE_USER");

        // 존재하지 않는 ID로 검색
        Optional<User> nonExistentUser = userService.findOne(9999L);
        assertThat(nonExistentUser).isEmpty();
    }

    @Test
    void findByUsername() {
        //given: 준비
        User user = new User();
        user.setEmail("test1@test1.com");
        user.setPassword("testPassword");
        user.setUsername("test1");
        user.setRoles("ROLE_USER");

        Long joinedUserId = userService.join(user);
        //when : 실행
        Optional<User> foundUser = userService.findByUsername(user.getUsername());
        //then : 검증
        assertThat(foundUser).isPresent();

        User retrievedUser = foundUser.get();
        assertThat(retrievedUser.getId()).isEqualTo(joinedUserId);
        assertThat(retrievedUser.getEmail()).isEqualTo("test1@test1.com");
        assertThat(retrievedUser.getUsername()).isEqualTo("test1");
        assertThat(retrievedUser.getRoles()).isEqualTo("ROLE_USER");


        // 존재하지 않는 ID로 검색
       Optional<User> nonExistentUser = userService.findByUsername("nonExistUsername");
       assertThat(nonExistentUser).isEmpty();
    }

}