package HomePage.repository;

import HomePage.domain.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JdbcTemplateUserRepository implements UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateUserRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    private String tableName = "security.user";

    @Override
    public User save(User user) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(tableName).usingGeneratedKeyColumns("id");
        Timestamp createDate = new Timestamp(System.currentTimeMillis());
        Timestamp loginDate = new Timestamp(System.currentTimeMillis());
        user.setCreateDate(createDate);
        user.setLoginDate(loginDate);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("username", user.getUsername());
        parameters.put("password", user.getPassword());
        parameters.put("email", user.getEmail());
        parameters.put("phoneNumber", user.getPhoneNumber());
        parameters.put("role", user.getRoles());
        parameters.put("createDate",user.getCreateDate() );
        parameters.put("loginDate", user.getLoginDate());
        parameters.put("provider", user.getProvider());
        parameters.put("providerId", user.getProviderId());

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        user.setId(key.longValue());

        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = String.format("SELECT * FROM %s where id = ?", tableName);
        List<User> result = jdbcTemplate.query(sql, memberRowMapper(), id);
        // 리스트의 병렬 검색을 위해서 stream()을 사용
        // Optional<>을 사용하는 이유로는 null 값이 오면 발생하는 Null Pointer Exception을 막기 위함이며. 결국 허용함에 따라 메모리를 필요로 하는데
        // 여러개의 null 객체를 생성하더라도 static 키워드를 통해서 빈 객체를 공유함으로써 메모리를 절약할 수 있도록 설계가 되어있음.
        return result.stream().findAny();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = String.format("SELECT * FROM %s where username = ?", tableName);
        List<User> result = jdbcTemplate.query(sql, memberRowMapper(), username);
        System.out.println(result);
        return result.stream().findAny();
    }

    @Override
    public List<User> findAll() {
        String sql = String.format("SELECT * FROM %s", tableName);
        return jdbcTemplate.query(sql, memberRowMapper());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = String.format("SELECT * FROM %s where email = ?", tableName);
        List<User> result = jdbcTemplate.query(sql, memberRowMapper(), email);
        System.out.println(result);
        return result.stream().findAny();
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        String sql = String.format("SELECT * FROM %s where phoneNumber = ?", tableName);
        List<User> result = jdbcTemplate.query(sql, memberRowMapper(), phoneNumber);
        return result.stream().findAny();
    }

    private RowMapper<User> memberRowMapper(){
        return (rs, rowNum) -> {
            // 멤버 인스턴스 생성, 검색 용도로만 사용하고 이는 저장하려는 것이 아님.
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setEmail(rs.getString("email"));
            user.setPhoneNumber(rs.getString("phoneNumber"));
            user.setRoles(rs.getString("Role"));
            return user;
        };
    }
    @Override
    public void setTableName(String tableName){
        this.tableName = tableName;
    }
}
