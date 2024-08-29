package HomePage.repository;

import HomePage.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JdbcTemplateUserRepositoryUnitTest {
    @Mock
    private JdbcTemplate mockJdbcTemplate;
    private JdbcTemplateUserRepository userRepository;
    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository = new JdbcTemplateUserRepository(mockJdbcTemplate);
        ReflectionTestUtils.setField(userRepository, "tableName", "test.user");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setEmail("test@test.com");
        testUser.setPassword("password");
        testUser.setRoles("ROLE_USER");
    }

    @Test
    void save() throws SQLException {
        // given
        when(mockJdbcTemplate.update(any(), any(KeyHolder.class))).thenAnswer(invocation -> {
            KeyHolder keyHolder = invocation.getArgument(1); // update의 두번째 인자인 keyHolder에 접근 (인덱스 1)
            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put("id", 1L);
            ((GeneratedKeyHolder) keyHolder).getKeyList().add(keyMap);
            return 1;
        });

        // when
        User savedUser = userRepository.save(testUser);

        // then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isEqualTo(1L);

        ArgumentCaptor<PreparedStatementCreator> pscCaptor = ArgumentCaptor.forClass(PreparedStatementCreator.class);
        verify(mockJdbcTemplate).update(pscCaptor.capture(), any(KeyHolder.class));

        // PreparedStatementCreator 검증
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPs = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(anyString(), any(String[].class))).thenReturn(mockPs);

        pscCaptor.getValue().createPreparedStatement(mockConnection);

        verify(mockPs).setString(1, testUser.getUsername());
        verify(mockPs).setString(2, testUser.getPassword());
        verify(mockPs).setString(3, testUser.getEmail());
        verify(mockPs).setString(4, testUser.getRoles());
        verify(mockPs).setString(5, testUser.getPhoneNumber());
        verify(mockPs).setString(6, testUser.getProvider());
        verify(mockPs).setString(7, testUser.getProviderId());
    }

    @Test
    void saveWithDuplicateUsername() {
        // given
        when(mockJdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class)))
            .thenThrow(new DuplicateKeyException("Duplicate username"));

        // when & then
        assertThatThrownBy(() -> userRepository.save(testUser))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessageContaining("Duplicate username");

        // verify that update method was called
        verify(mockJdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));
    }

    @Test
    void findAll() {
        String sql = String.format("SELECT * FROM %s", userRepository.getTableName());
        List<User> expectedUsers = Arrays.asList(testUser);
        when(mockJdbcTemplate.query(eq(sql), any(RowMapper.class)))
                .thenReturn(expectedUsers);

        List<User> actualUsers = userRepository.findAll();

        assertThat(actualUsers).isEqualTo(expectedUsers);
    }

    @Test
    void findById() {
        //given : 준비
        String sql = String.format("SELECT * FROM %s WHERE id = ?", userRepository.getTableName());
        when(mockJdbcTemplate.query(
                eq(sql),
                any(RowMapper.class),
                eq(1L)
        )).thenReturn(Collections.singletonList(testUser));

        //when : 실행
        Optional<User> foundUser = userRepository.findById(1L);

        //then : 검증
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(testUser);
        verify(mockJdbcTemplate).query(eq(sql), any(RowMapper.class), eq(1L));
    }

    @Test
    void findByIdNotFound() {
        //given : 준비
        String sql = String.format("SELECT * FROM %s WHERE id = ?", userRepository.getTableName());
        when(mockJdbcTemplate.query(
                eq(sql),
                any(RowMapper.class),
                eq(9999L) // 9999는 없는 USER id라고 가정
        )).thenReturn(Collections.emptyList());
        //when : 실행
        Optional<User> foundUser = userRepository.findById(9999L);
        //then : 검증
        assertThat(foundUser).isEmpty();
        verify(mockJdbcTemplate).query(eq(sql), any(RowMapper.class), eq(9999L));
    }

    @Test
    void findByUsername() {
        //given : 준비
        String sql = String.format("SELECT * FROM %s WHERE username = ?", userRepository.getTableName());
        when(mockJdbcTemplate.query(
                eq(sql),
                any(RowMapper.class),
                eq("testUser")
        )).thenReturn(Collections.singletonList(testUser));
        //when : 실행
        Optional<User> foundUser = userRepository.findByUsername("testUser");
        //then : 검증
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(testUser);
        verify(mockJdbcTemplate).query(eq(sql), any(RowMapper.class), eq("testUser"));
    }

    @Test
    void findByUsernameNotFound() {
        //given : 준비
        String sql = String.format("SELECT * FROM %s WHERE username = ?", userRepository.getTableName());
        when(mockJdbcTemplate.query(
                eq(sql),
                any(RowMapper.class),
                eq("nonExistentUser")
        )).thenReturn(Collections.emptyList());
        //when : 실행
        Optional<User> foundUser = userRepository.findByUsername("nonExistentUser");
        //then : 검증
        assertThat(foundUser).isEmpty();
        verify(mockJdbcTemplate).query(eq(sql), any(RowMapper.class), eq("nonExistentUser"));
    }

    @Test
    void findByEmail() {
        //given : 준비
        String sql = String.format("SELECT * FROM %s WHERE email = ?", userRepository.getTableName());
        when(mockJdbcTemplate.query(
                eq(sql),
                any(RowMapper.class),
                eq(testUser.getEmail())
        )).thenReturn(Collections.singletonList(testUser));
        //when : 실행
        Optional<User> foundUser = userRepository.findByEmail("test@test.com");
        //then : 검증
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(testUser);
        verify(mockJdbcTemplate).query(eq(sql), any(RowMapper.class), eq(testUser.getEmail()));
    }

    @Test
    void findByEmailNotFound() {
        //given : 준비
        String sql = String.format("SELECT * FROM %s WHERE email = ?", userRepository.getTableName());
        when(mockJdbcTemplate.query(
                eq(sql),
                any(RowMapper.class),
                eq("nonexistent@test.com")
        )).thenReturn(Collections.emptyList());

        //when : 실행
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@test.com");
        //검증
        assertThat(foundUser).isEmpty();
        verify(mockJdbcTemplate).query(eq(sql), any(RowMapper.class), eq("nonexistent@test.com"));
    }
}