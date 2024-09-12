package HomePage.repository;

import HomePage.domain.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
        String sql = "INSERT INTO " + tableName + " (username, password, email, role, phoneNumber, provider, providerId, createdAt) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        user.setCreateDate(new Timestamp(System.currentTimeMillis()));
        jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getRoles());
                ps.setString(5, user.getPhoneNumber());
                ps.setString(6, user.getProvider());
                ps.setString(7, user.getProviderId());
                ps.setTimestamp(8, user.getCreateDate());
               return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = String.format("SELECT * FROM %s WHERE id = ?", tableName);
        List<User> result = jdbcTemplate.query(sql, memberRowMapper(), id);
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

    @Override
    public int count() {
        String sql = String.format("SELECT COUNT(*) FROM %s", tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public int countById(Long id) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE id = ?", tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class, id);
    }

    @Override
    public int countByUsername(String username) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE LOWER(username) LIKE LOWER(?)", tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class, username);
    }

    @Override
    public int countByEmail(String email) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE LOWER(email) LIKE LOWER(?)", tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class, email);
    }

    @Override
    public int countByRole(String role) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE LOWER(role) LIKE LOWER(?)", tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class, role);
    }

    @Override
    public List<User> findUserPage(int offset, int limit) {
        String sql = String.format("SELECT * FROM %s ORDER BY id DESC LIMIT ? OFFSET ?", tableName);
        return jdbcTemplate.query(sql, memberRowMapper(), limit, offset);
    }

    @Override
    public List<User> findUserPageById(int offset, int limit, Long id) {
        String sql = String.format("SELECT * FROM %s WHERE id = ? ORDER BY id DESC LIMIT ? OFFSET ?", tableName);
        return jdbcTemplate.query(sql, memberRowMapper(), id, limit, offset);
    }

    @Override
    public List<User> findUserPageByUsername(int offset, int limit, String username) {
        String sql = String.format("SELECT * FROM %s WHERE LOWER(username) LIKE LOWER(?) ORDER BY id DESC LIMIT ? OFFSET ?", tableName);
        return jdbcTemplate.query(sql, memberRowMapper(), "%" + username + "%", limit, offset);
    }

    @Override
    public List<User> findUserPageByEmail(int offset, int limit, String email) {
        String sql = String.format("SELECT * FROM %s WHERE LOWER(email) LIKE LOWER(?) ORDER BY id DESC LIMIT ? OFFSET ?", tableName);
        return jdbcTemplate.query(sql, memberRowMapper(),"%" + email + "%", limit, offset);
    }

    @Override
    public List<User> findUserPageByRole(int offset, int limit, String role) {
        String sql = String.format("SELECT * FROM %s WHERE LOWER(role) LIKE LOWER(?) ORDER BY id DESC LIMIT ? OFFSET ?", tableName);
        return jdbcTemplate.query(sql, memberRowMapper(), "%" + role + "%", limit, offset);
    }

    @Override
    public int countByCreatedAtAfter(LocalDateTime date) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE createdAt > ?", tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class, Timestamp.valueOf(date));
    }

    @Override
    public int countByCreatedAtBefore(LocalDateTime date) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE createdAt < ?", tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class, Timestamp.valueOf(date));
    }

    @Override
    public int countByLastLoginAfter(LocalDateTime date) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE lastLogin > ?", tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class, Timestamp.valueOf(date));
    }

    @Override
    public int countByDeletedAtAfter(LocalDateTime date) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE deletedAt > ?", tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class, Timestamp.valueOf(date));
    }

    @Override
    public int countByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE createdAt BETWEEN ? AND ?", tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class, Timestamp.valueOf(start), Timestamp.valueOf(end));
    }

    @Override
    public int countByDeletedAtBetween(LocalDateTime start, LocalDateTime end) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE deletedAt BETWEEN ? AND ?", tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class, Timestamp.valueOf(start), Timestamp.valueOf(end));
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
            user.setProvider(rs.getString("provider"));
            user.setProviderId(rs.getString("providerId"));
            user.setCreateDate(rs.getTimestamp("createdAt"));
            user.setLoginDate(rs.getTimestamp("lastLogin"));
            user.setDeleteDate(rs.getTimestamp("deletedAt"));
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
