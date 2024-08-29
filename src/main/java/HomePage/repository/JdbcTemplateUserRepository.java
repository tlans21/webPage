package HomePage.repository;

import HomePage.domain.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

public class JdbcTemplateUserRepository implements UserRepository {
    private JdbcTemplate jdbcTemplate;
    private String tableName = "security.user"; // 기본 테이블 값
    public JdbcTemplateUserRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // UnitTest에 Mock JdbcTemplate을 한번에 주입 받기 위한 생성자
    public JdbcTemplateUserRepository(JdbcTemplate jdbcTemplate) {
           this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public User save(User user) {
        String sql = "INSERT INTO " + tableName + " (username, password, email, role, phoneNumber, provider, providerId) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getRoles());
                ps.setString(5, user.getPhoneNumber());
                ps.setString(6, user.getProvider());
                ps.setString(7, user.getProviderId());
               return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = String.format("SELECT * FROM %s WHERE id = ?", tableName);
        List<User> result = jdbcTemplate.query(sql, memberRowMapper(), id);
        // 리스트의 병렬 검색을 위해서 stream()을 사용
        // Optional<>을 사용하는 이유로는 null 값이 오면 발생하는 Null Pointer Exception을 막기 위함이며. 결국 허용함에 따라 메모리를 필요로 하는데
        // 여러개의 null 객체를 생성하더라도 static 키워드를 통해서 빈 객체를 공유함으로써 메모리를 절약할 수 있도록 설계가 되어있음.
        return result.stream().findAny();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = String.format("SELECT * FROM %s WHERE username = ?", tableName);
        List<User> result = jdbcTemplate.query(sql, memberRowMapper(), username);
        return result.stream().findAny();
    }

    @Override
    public List<User> findAll() {
        String sql = String.format("SELECT * FROM %s", tableName);
        return jdbcTemplate.query(sql, memberRowMapper());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = String.format("SELECT * FROM %s WHERE email = ?", tableName);
        List<User> result = jdbcTemplate.query(sql, memberRowMapper(), email);
        return result.stream().findAny();
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        String sql = String.format("SELECT * FROM %s WHERE phoneNumber = ?", tableName);
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


    public String getTableName(){
        return this.tableName;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

}
