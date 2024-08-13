package HomePage.repository;

import HomePage.domain.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class JdbcTemplateUserRepositoryTest {
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
    void findAll() {
        //given : 준비
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@test.com");
        user1.setPassword("password1");
        user1.setRoles("USER");
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@test.com");
        user2.setPassword("password2");
        user2.setRoles("ADMIN");
        userRepository.save(user2);

        //when : 실행
        List<User> users = userRepository.findAll();


        //then : 검증
        assertThat(users).isNotNull();
        assertThat(users.size()).isEqualTo(2); // 앞에서
        assertThat(users).extracting("username").containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    void save() {
        //given : 준비
        User user = new User();

        user.setUsername("tlans");
        user.setEmail("tlans21@naver.com");
        user.setPassword("1234");
        user.setRoles("USER");

        //when : 실행
        User savedUser = userRepository.save(user);

        //then : 검증
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(savedUser.getRoles()).isEqualTo(user.getRoles());
        assertThat(savedUser.getPassword()).isEqualTo("1234");

    }

    @Test
    void findById() {
        //given : 준비
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setRoles("USER");
        User savedUser = userRepository.save(user);
        //when : 실행
        User foundUser = userRepository.findById(savedUser.getId()).get();
        //then : 검증
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.getUsername()).isEqualTo("testUser");
    }

    @Test
    void findByUsername() {
        //given : 준비
        User user = new User();
        user.setUsername("findByUsernameTest");
        user.setEmail("findByUsername@test.com");
        user.setPassword("password");
        user.setRoles("USER");
        userRepository.save(user);
        //when : 실행
        User foundUser = userRepository.findByUsername(user.getUsername()).get();
        //then : 검증
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    void findByEmail() {
        //given : 준비
        User user = new User();
        user.setUsername("emailTest");
        user.setEmail("findByEmail@test.com");
        user.setPassword("password");
        user.setRoles("USER");
        userRepository.save(user);

        //when : 실행
        User foundUser = userRepository.findByEmail("findByEmail@test.com").get();


        //then : 검증
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("findByEmail@test.com");
    }

    @Test
    void findByPhoneNumber() {

    }
}