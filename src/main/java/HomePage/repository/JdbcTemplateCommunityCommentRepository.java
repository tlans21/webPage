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

    public JdbcTemplateCommunityCommentRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public CommunityComment save(CommunityComment comment) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("communitycomment").usingGeneratedKeyColumns("comment_id");
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
        String sql = "SELECT COUNT(*) FROM security.communitycomment WHERE board_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, boardId);
    }

    @Override
    public List<CommunityComment> selectAll() {
        String sql = "select * from security.communitycomment";
        return jdbcTemplate.query(sql, communityCommentRowMapper());
    }

    @Override
    public boolean update(CommunityComment comment) {
        String sql = "UPDATE * FROM security.communitycomment SET content = ?, updateDate = ?, WHERE comment_id = ?";
        Timestamp updateDate = new Timestamp(System.currentTimeMillis());
        int isUpdate = jdbcTemplate.update(sql, comment.getContent(), updateDate, comment.getId());
        if (isUpdate == 0 || isUpdate > 1){
           return false;
        }
        return true;
    }

    @Override
    public Optional<CommunityComment> findCommentById(Long commentId) {
        List<CommunityComment> result = jdbcTemplate.query("select * from security.communtiycomment where id = ?", communityCommentRowMapper(), commentId);
        return result.stream().findAny();
    }

    @Override
    public List<CommunityComment> selectById(Long boardId) {
        List<CommunityComment> result = jdbcTemplate.query("select * from security.communitycomment where board_id = ?", communityCommentRowMapper(), boardId);
        return result;
    }

    @Override
    public boolean deleteByWriter(String writer) {
        int isDelete = jdbcTemplate.update("DELETE FROM security.communitycomment WHERE writer = ?", writer);
        return isDelete == 1;
    }

    @Override
    public boolean deleteByCommentId(Long id) {
        int isDelete = jdbcTemplate.update("DELETE FROM security.communitycomment WHERE id = ?", id);
        return isDelete == 1;
    }

    @Override
    public boolean deleteByBoardId(Long id) {
        int isDelete = jdbcTemplate.update("DELETE FROM security.communitycomment WHERE board_id = ?", id);
        return isDelete >= 1;
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
}
