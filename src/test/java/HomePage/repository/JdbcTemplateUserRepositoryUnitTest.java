package HomePage.repository;

import HomePage.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JdbcTemplateUserRepositoryUnitTest {
    @Mock
    private JdbcTemplate mockJdbcTemplate;
    private JdbcTemplateUserRepository mockUserRepository;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockUserRepository = new JdbcTemplateUserRepository(mockJdbcTemplate);
        ReflectionTestUtils.setField(mockUserRepository, "tableName", "test.user");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setEmail("test@test.com");
        testUser.setPassword("password");
        testUser.setRoles("ROLE_USER");
    }

    @Test
    void save() {
        when(mockJdbcTemplate.update(
                anyString(),
                any(MapSqlParameterSource.class),
                any(KeyHolder.class),
                eq(new String[]{"id"})
        )).thenAnswer(invocation -> {
            KeyHolder keyHolder = invocation.getArgument(2); // 첫 번째 인자 (인덱스 0): SQL 문자열, 두 번째 인자 (인덱스 1): MapSqlParameterSource 객체, 세 번째 인자 (인덱스 2): KeyHolder 객체, 네 번째 인자 (인덱스 3): 생성된 키의 컬럼 이름 배열
            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put("id", 1L);
            ((GeneratedKeyHolder) keyHolder).getKeyList().add(keyMap);
            return 1;
        });

        // when : 실행
        User savedUser = mockUserRepository.save(testUser);

        // then : 검증
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isEqualTo(1L);

        verify(mockJdbcTemplate).update(
                anyString(),
                any(MapSqlParameterSource.class),
                any(KeyHolder.class),
                eq(new String[]{"id"})
        );
    }

    @Test
    void findAll() {
        String sql = String.format("SELECT * FROM %s", mockUserRepository.getTableName());
        List<User> expectedUsers = Arrays.asList(testUser);
        when(mockJdbcTemplate.query(eq(sql), any(RowMapper.class)))
                .thenReturn(expectedUsers);

        List<User> actualUsers = mockUserRepository.findAll();

        assertThat(actualUsers).isEqualTo(expectedUsers);
    }

    @Test
    void saveWithDuplicateUsername() {
        when(mockJdbcTemplate.update(
                anyString(),
                any(MapSqlParameterSource.class),
                any(KeyHolder.class),
                eq(new String[]{"id"})
        )).thenThrow(new DuplicateKeyException("Duplicate username"));

        assertThatThrownBy(() -> mockUserRepository.save(testUser))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessageContaining("Duplicate username");
    }

    @Test
    void findById() {
        //given : 준비
        String sql = String.format("SELECT * FROM %s WHERE id = ?", mockUserRepository.getTableName());
        when(mockJdbcTemplate.query(
                eq(sql),
                any(RowMapper.class),
                eq(1L)
        )).thenReturn(Collections.singletonList(testUser));

        //when : 실행
        Optional<User> foundUser = mockUserRepository.findById(1L);

        //then : 검증
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(testUser);
        verify(mockJdbcTemplate).query(eq(sql), any(RowMapper.class), eq(1L));
    }

    @Test
    void findByIdNotFound() {
        //given : 준비
        String sql = String.format("SELECT * FROM %s WHERE id = ?", mockUserRepository.getTableName());
        when(mockJdbcTemplate.query(
                eq(sql),
                any(RowMapper.class),
                eq(9999L) // 9999는 없는 USER id라고 가정
        )).thenReturn(Collections.emptyList());
        //when : 실행
        Optional<User> foundUser = mockUserRepository.findById(9999L);
        //then : 검증
        assertThat(foundUser).isEmpty();
        verify(mockJdbcTemplate).query(eq(sql), any(RowMapper.class), eq(9999L));
    }

    @Test
    void findByUsername() {
        //given : 준비
        String sql = String.format("SELECT * FROM %s WHERE username = ?", mockUserRepository.getTableName());
        when(mockJdbcTemplate.query(
                eq(sql),
                any(RowMapper.class),
                eq("testUser")
        )).thenReturn(Collections.singletonList(testUser));
        //when : 실행
        Optional<User> foundUser = mockUserRepository.findByUsername("testUser");
        //then : 검증
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(testUser);
        verify(mockJdbcTemplate).query(eq(sql), any(RowMapper.class), eq("testUser"));
    }

    @Test
    void findByUsernameNotFound() {
        //given : 준비
        String sql = String.format("SELECT * FROM %s WHERE username = ?", mockUserRepository.getTableName());
        when(mockJdbcTemplate.query(
                eq(sql),
                any(RowMapper.class),
                eq("nonExistentUser")
        )).thenReturn(Collections.emptyList());
        //when : 실행
        Optional<User> foundUser = mockUserRepository.findByUsername("nonExistentUser");
        //then : 검증
        assertThat(foundUser).isEmpty();
        verify(mockJdbcTemplate).query(eq(sql), any(RowMapper.class), eq("nonExistentUser"));
    }

    @Test
    void findByEmail() {
        //given : 준비
        String sql = String.format("SELECT * FROM %s WHERE email = ?", mockUserRepository.getTableName());
        when(mockJdbcTemplate.query(
                eq(sql),
                any(RowMapper.class),
                eq(testUser.getEmail())
        )).thenReturn(Collections.singletonList(testUser));
        //when : 실행
        Optional<User> foundUser = mockUserRepository.findByEmail("test@test.com");
        //then : 검증
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(testUser);
        verify(mockJdbcTemplate).query(eq(sql), any(RowMapper.class), eq(testUser.getEmail()));
    }

    @Test
    void findByEmailNotFound() {
        //given : 준비
        String sql = String.format("SELECT * FROM %s WHERE email = ?", mockUserRepository.getTableName());
        when(mockJdbcTemplate.query(
                eq(sql),
                any(RowMapper.class),
                eq("nonexistent@test.com")
        )).thenReturn(Collections.emptyList());

        //when : 실행
        Optional<User> foundUser = mockUserRepository.findByEmail("nonexistent@test.com");
        //검증
        assertThat(foundUser).isEmpty();
        verify(mockJdbcTemplate).query(eq(sql), any(RowMapper.class), eq("nonexistent@test.com"));
    }
}