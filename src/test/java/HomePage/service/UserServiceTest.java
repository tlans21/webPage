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

import static org.assertj.core.api.Assertions.assertThat;

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
        user.setRoles("USER");
        //when : 실행
        Long joinedUserId = userService.join(user);
        //then : 검증
        User foundUser = userService.findOne(joinedUserId).orElse(null);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("test");
        assertThat(foundUser.getEmail()).isEqualTo("test@test.com");
        assertThat(foundUser.getRoles()).isEqualTo("USER");
        assertThat(foundUser.getPassword()).isNotEmpty();
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
    }

    @Test
    void findMembers() {
    }

    @Test
    void findOne() {
    }

    @Test
    void findByUsername() {
    }

    @Test
    void authenticateMember() {
    }
}