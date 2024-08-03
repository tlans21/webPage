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
        parameters.put("writer", comment.getWriter());
        parameters.put("content", comment.getContent());
        parameters.put("regdate", comment.getRegisterDate());

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        comment.setId(key.longValue());
        return comment;
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
    public Optional<CommunityComment> selectById(Long id) {
        List<CommunityComment> result = jdbcTemplate.query("select * from security.communitycomment where comment_id = ?", communityCommentRowMapper(), id);
        return result.stream().findAny();
    }

    @Override
    public boolean deleteByWriter(String writer) {
        int isDelete = jdbcTemplate.update("DELETE FROM security.communitycomment WHERE writer = ?", writer);
        if (isDelete == 0 || isDelete > 1){
            return false;
        }
        return true;
    }
    private RowMapper<CommunityComment> communityCommentRowMapper(){
       return (rs, rowNum) -> {
           CommunityComment communityComment = new CommunityComment();
           communityComment.setId(rs.getLong("comment_id"));
           communityComment.setContent(rs.getString("content"));
           communityComment.setWriter(rs.getString("writer"));
           communityComment.setRegisterDate(rs.getTimestamp("regdate"));
           communityComment.setUpdateDate(rs.getTimestamp("updateDate"));
           communityComment.setDeleteDate(rs.getTimestamp("deleteDate"));
           return communityComment;
       };
   }
}
