package HomePage.repository;

import HomePage.domain.model.entity.CommunityComment;
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

public class JdbcTemplateCommunityCommentRepository implements CommentRepository<CommunityComment>{
    private final JdbcTemplate jdbcTemplate;
    private String tableName = "security.communitycomment"; // 기본 테이블 값
    public JdbcTemplateCommunityCommentRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public JdbcTemplateCommunityCommentRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CommunityComment save(CommunityComment comment) {
        String sql = "INSERT INTO " + tableName + " (writer, board_id, content, createdAt, updatedAt, deletedAt, like_count, dislike_count) " +
                                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        comment.setRegisterDate(new Timestamp(System.currentTimeMillis()));
        jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"comment_id"});
                ps.setString(1, comment.getWriter());
                ps.setLong(2, comment.getBoardId());
                ps.setString(3, comment.getContent());
                ps.setTimestamp(4, comment.getRegisterDate());
                ps.setTimestamp(5, comment.getUpdateDate());
                ps.setTimestamp(6, comment.getDeleteDate());
                ps.setInt(7, comment.getLikeCount());
                ps.setInt(8, comment.getLikeCount());
               return ps;
        }, keyHolder);

        comment.setId(keyHolder.getKey().longValue());
        return comment;
    }

    @Override
    public int count() {
        String sql = String.format("SELECT COUNT(*) FROM %s", tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public int countByCreatedAtAfter(LocalDateTime date) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE createdAt > ?", tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class, Timestamp.valueOf(date));
    }


    @Override
    public int countByBoardId(Long boardId) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE board_id = ?", tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class, boardId);
    }

    @Override
    public int countByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE createdAt BETWEEN ? AND ?", tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class, start, end);
    }

    @Override
    public List<CommunityComment> selectAll() {
        String sql = String.format("SELECT * FROM %s", tableName);
        return jdbcTemplate.query(sql, communityCommentRowMapper());
    }

    @Override
    public boolean update(CommunityComment comment) {
        String sql = String.format("UPDATE %s SET content = ?, updateDate = ? WHERE comment_id = ?", tableName);
        Timestamp updateDate = new Timestamp(System.currentTimeMillis());
        int isUpdate = jdbcTemplate.update(sql, comment.getContent(), updateDate, comment.getId());
        return isUpdate == 1;
    }

    @Override
    public Optional<CommunityComment> findCommentById(Long commentId) {
        String sql = String.format("SELECT * FROM %s WHERE comment_id = ?", tableName);
        List<CommunityComment> result = jdbcTemplate.query(sql, communityCommentRowMapper(), commentId);
        return result.stream().findAny();
    }

    @Override
    public List<CommunityComment> selectByBoardId(Long boardId) {
        String sql = String.format("SELECT * FROM %s WHERE board_id = ?", tableName);
        return jdbcTemplate.query(sql, communityCommentRowMapper(), boardId);
    }
    @Override
    public List<CommunityComment> selectByWriter(String writer){
        String sql = String.format("SELECT * FROM %s WHERE writer = ?", tableName);
        return jdbcTemplate.query(sql, communityCommentRowMapper(), writer);
    }

    @Override
    public List<CommunityComment> selectRecentByWriter(String writer, int limit) {
        String sql = String.format("SELECT * FROM %s WHERE writer = ? ORDER BY createdAt DESC LIMIT ?", tableName);
        return jdbcTemplate.query(sql, communityCommentRowMapper(), writer, limit);
    }

    @Override
    public boolean deleteByWriter(String writer) {
        String sql = String.format("DELETE FROM %s WHERE writer = ?", tableName);
        int isDelete = jdbcTemplate.update(sql, writer);
        return isDelete >= 1;
    }

    @Override
    public boolean deleteByCommentId(Long id) {
        String sql = String.format("DELETE FROM %s WHERE comment_id = ?", tableName);
        int isDelete = jdbcTemplate.update(sql, id);
        return isDelete == 1;
    }

    @Override
    public boolean deleteByBoardId(Long id) {
        String sql = String.format("DELETE FROM %s WHERE board_id = ?", tableName);
        int isDelete = jdbcTemplate.update(sql, id);
        System.out.println(isDelete);
        return isDelete >= 0;
    }


    private RowMapper<CommunityComment> communityCommentRowMapper(){
       return (rs, rowNum) -> {
           CommunityComment communityComment = new CommunityComment();
           communityComment.setId(rs.getLong("comment_id"));
           communityComment.setBoardId(rs.getLong("board_id"));
           communityComment.setContent(rs.getString("content"));
           communityComment.setWriter(rs.getString("writer"));
           communityComment.setRegisterDate(rs.getTimestamp("createdAt"));
           communityComment.setUpdateDate(rs.getTimestamp("updatedAt"));
           communityComment.setDeleteDate(rs.getTimestamp("deletedAt"));
           communityComment.setLikeCount(rs.getInt("like_count"));
           communityComment.setDislikeCount(rs.getInt("dislike_count"));
           return communityComment;
       };
    }
    @Override
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName(){
        return this.tableName;
    }
}
