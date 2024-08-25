package HomePage.repository;

import HomePage.domain.model.CommunityComment;
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

public class JdbcTemplateCommunityCommentRepository implements CommentRepository<CommunityComment>{
    private final JdbcTemplate jdbcTemplate;
    private String tableName = "security.communitycomment"; // 기본 테이블 값
    public JdbcTemplateCommunityCommentRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public CommunityComment save(CommunityComment comment) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(tableName).usingGeneratedKeyColumns("comment_id");
        Timestamp regDate = new Timestamp(System.currentTimeMillis());
        comment.setRegisterDate(regDate);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("board_id", comment.getBoard_id());
        parameters.put("writer", comment.getWriter());
        parameters.put("content", comment.getContent());
        parameters.put("regdate", comment.getRegisterDate());

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        comment.setId(key.longValue());
        return comment;
    }

    @Override
    public int countByBoardId(Long boardId) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE board_id = ?", tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class, boardId);
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
    public List<CommunityComment> selectById(Long boardId) {
        String sql = String.format("SELECT * FROM %s WHERE board_id = ?", tableName);
        return jdbcTemplate.query(sql, communityCommentRowMapper(), boardId);
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
           communityComment.setBoard_id(rs.getLong("board_id"));
           communityComment.setContent(rs.getString("content"));
           communityComment.setWriter(rs.getString("writer"));
           communityComment.setRegisterDate(rs.getTimestamp("regdate"));
           communityComment.setUpdateDate(rs.getTimestamp("updateDate"));
           communityComment.setDeleteDate(rs.getTimestamp("deleteDate"));
           return communityComment;
       };
   }
   @Override
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
