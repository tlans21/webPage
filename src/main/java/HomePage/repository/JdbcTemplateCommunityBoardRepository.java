package HomePage.repository;

import HomePage.domain.model.entity.CommunityBoard;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JdbcTemplateCommunityBoardRepository implements BoardRepository<CommunityBoard> {
    private final JdbcTemplate jdbcTemplate;
    private String tableName = "security.communityboard"; // 기본 테이블 이름;
    public JdbcTemplateCommunityBoardRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public CommunityBoard save(CommunityBoard communityBoard) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(tableName).usingGeneratedKeyColumns("board_id");
        Timestamp regDate = new Timestamp(System.currentTimeMillis());
        communityBoard.setRegisterDate(regDate);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("writer", communityBoard.getWriter());
        parameters.put("title", communityBoard.getTitle());
        parameters.put("content", communityBoard.getContent());
        parameters.put("regdate", communityBoard.getRegisterDate());
        parameters.put("viewCnt", communityBoard.getViewCnt());
        parameters.put("commentCnt", communityBoard.getCommentCnt());

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        communityBoard.setId(key.longValue());
        return communityBoard;
    }

    @Override
    public List<CommunityBoard> findPage(int offset, int limit) {
        String sql = String.format("SELECT * FROM %s WHERE deletedAt IS NULL ORDER BY board_id DESC LIMIT ? OFFSET ?", tableName);
        return jdbcTemplate.query(sql, communityBoardRowMapper(), limit, offset); // offset은 건너띄려는 행, limit는 조회하려는 갯수
    }
    @Override
    public List<CommunityBoard> findPageOrderByTopView(int offset, int limit) {
        String sql = String.format("SELECT * FROM %s ORDER BY viewCnt DESC LIMIT ? OFFSET ?", tableName);
        return jdbcTemplate.query(sql, communityBoardRowMapper(), limit, offset);
    }

    @Override
    public List<CommunityBoard> findPageOrderByTopCommentCnt(int offset, int limit) {
        String sql = String.format("SELECT * FROM %s ORDER BY commentCnt DESC LIMIT ? OFFSET ?", tableName);
        return jdbcTemplate.query(sql, communityBoardRowMapper(), limit, offset);
    }
    @Override
    public List<CommunityBoard> findPageByTitle(int offset, int limit, String title){
        String sql = String.format("SELECT * FROM %s WHERE deletedAt IS NULL AND title LIKE ? ORDER BY regdate DESC LIMIT ? OFFSET ?", tableName);
        return jdbcTemplate.query(sql, communityBoardRowMapper(), "%" + title + "%", limit, offset);
    }
    @Override
    public List<CommunityBoard> findPageByWriter(int offset, int limit, String writer){
        String sql = String.format("SELECT * FROM %s WHERE deletedAt IS NULL AND writer = ? ORDER BY regdate DESC LIMIT ? OFFSET ?", tableName);
        return jdbcTemplate.query(sql, communityBoardRowMapper(), writer, limit, offset);
    }
    @Override
    public int count() {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE deletedAt IS NULL", tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
    @Override
    public int countByTitle(String title) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE title LIKE ?", tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class, "%" + title + "%");
    }
    @Override
    public int countByWriter(String writer){
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE writer = ?", tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class, writer);
    }

    @Override
    public int countByCreatedAtAfter(LocalDateTime date) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE createdAt > ?", tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class, date);
    }

    @Override
    public int countByCreatedAtBefore(LocalDateTime date) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE createdAt < ?", tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class, date);
    }

    @Override
    public int countByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE createdAt BETWEEN ? AND ?", tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class, start, end);
    }

    @Override
    public boolean update(CommunityBoard communityBoard) {
        String sql = String.format("UPDATE %s SET title = ?, content = ?, updatedAt = ? WHERE board_id = ?", tableName);
        Timestamp updateDate = new Timestamp(System.currentTimeMillis());
        int isUpdate = jdbcTemplate.update(sql, communityBoard.getTitle(), communityBoard.getContent(), updateDate, communityBoard.getId());
        if (isUpdate == 0 || isUpdate > 1){
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = String.format("DELETE FROM %s WHERE board_id = ?", tableName);
        int isDelete = jdbcTemplate.update(sql, id);
        return isDelete == 1;
    }

    @Override
    public Optional<CommunityBoard> selectById(Long id) {
        String sql = String.format("SELECT * FROM %s where board_id = ? ", tableName);
        List<CommunityBoard> result = jdbcTemplate.query(sql, communityBoardRowMapper(), id);
        return result.stream().findAny();
    }

    @Override
    public List<CommunityBoard> selectByTitle(String title) {
        String sql = String.format("SELECT * FROM %s WHERE title LIKE ?", tableName);
        String searchPattern = "%" + title + "%";
        List<CommunityBoard> result = jdbcTemplate.query(sql, communityBoardRowMapper(), searchPattern);
        return result;
    }

    @Override
    public List<CommunityBoard> selectByWriter(String writer) {
        String sql = String.format("SELECT * FROM %s WHERE writer = ?", tableName);
        List<CommunityBoard> result = jdbcTemplate.query(sql, communityBoardRowMapper(), writer);
        return result;
    }

    @Override
    public List<CommunityBoard> selectRecentByWriter(String writer, int limit) {
        String sql = String.format("SELECT * FROM %s WHERE writer = ? ORDER BY createdAt DESC LIMIT ?", tableName);
        List<CommunityBoard> result = jdbcTemplate.query(sql, communityBoardRowMapper(), writer, limit);
        return result;
    }

    @Override
    public List<CommunityBoard> selectAll() {
        String sql = String.format("SELECT * FROM %s ", tableName);
        return jdbcTemplate.query(sql, communityBoardRowMapper());
    }
    @Override
    public boolean incrementViews(Long id){
        String sql = String.format("UPDATE %s SET viewCnt = viewCnt + 1 where board_id = ?", tableName);
        int update = jdbcTemplate.update(sql, id);
        return update == 1;
    }
    public boolean updateCommentCnt(Long id, int commentCnt){
        String sql = String.format("UPDATE %s SET commentCnt = ? where board_id = ?", tableName);
        int update = jdbcTemplate.update(sql, commentCnt, id);
        return update == 1;
    }
    @Override
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    private RowMapper<CommunityBoard> communityBoardRowMapper(){
        return (rs, rowNum) -> {
            CommunityBoard communityBoard = new CommunityBoard();
            communityBoard.setId(rs.getLong("board_id"));
            communityBoard.setTitle(rs.getString("title"));
            communityBoard.setContent(rs.getString("content"));
            communityBoard.setWriter(rs.getString("writer"));
            communityBoard.setViewCnt(rs.getInt("viewCnt"));
            communityBoard.setRegisterDate(rs.getTimestamp("createdAt"));
            communityBoard.setUpdateDate(rs.getTimestamp("updatedAt"));
            communityBoard.setDeleteDate(rs.getTimestamp("deletedAt"));
            communityBoard.setCommentCnt(rs.getInt("commentCnt"));
            return communityBoard;
        };
    }
}
