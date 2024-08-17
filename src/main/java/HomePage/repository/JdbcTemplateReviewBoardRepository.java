package HomePage.repository;

import HomePage.domain.model.ReviewBoard;
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

public class JdbcTemplateReviewBoardRepository implements BoardRepository<ReviewBoard>{

    private final JdbcTemplate jdbcTemplate;
    private String tableName = "security.reviewBoard"; // 기본 테이블 이름
    public JdbcTemplateReviewBoardRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<ReviewBoard> findPage(int offset, int limit) {
        String sql = "SELECT * FROM security.reviewBoard WHERE deleteDate IS NULL ORDER BY registerDate DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, reviewBoardRowMapper(), limit, offset);
    }

    @Override
    public List<ReviewBoard> findPageOrderByTopView(int offset, int limit) {
        return null;
    }

    @Override
    public List<ReviewBoard> findPageOrderByTopCommentCnt(int offset, int limit) {
        return null;
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM security.reviewBoard WHERE deleteDate IS NULL";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public ReviewBoard save(ReviewBoard reviewBoard) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("board").usingGeneratedKeyColumns("board_id");
        Timestamp regDate = new Timestamp(System.currentTimeMillis());
        reviewBoard.setRegisterDate(regDate);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("writer", reviewBoard.getWriter());
        parameters.put("title", reviewBoard.getTitle());
        parameters.put("content", reviewBoard.getContent());
        parameters.put("regdate", reviewBoard.getRegisterDate());

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        reviewBoard.setId(key.longValue());
        return reviewBoard;
    }

    @Override
    public boolean update(ReviewBoard reviewBoard) {
        String sql = "UPDATE security.board SET title = ?, content = ?, updateDate = ? WHERE board_id = ?";
        Timestamp updateDate = new Timestamp(System.currentTimeMillis());
        int isUpdate = jdbcTemplate.update(sql, reviewBoard.getTitle(), reviewBoard.getContent(), updateDate, reviewBoard.getId());
        if (isUpdate == 0 || isUpdate > 1){
            return false;
        }
        return true;
    }


    @Override
    public boolean deleteById(Long id) {
        return false;
    }

    @Override
    public Optional<ReviewBoard> selectById(Long id) {
        List<ReviewBoard> result = jdbcTemplate.query("select * security.reviewBoard where id = ?", reviewBoardRowMapper(), id);
        return result.stream().findAny();
    }

    @Override
    public List<ReviewBoard> selectByTitle(String title) {
        List<ReviewBoard> result = jdbcTemplate.query("select * security.reviewBoard where title = ?", reviewBoardRowMapper(), title);
        return result;
    }

    @Override
    public List<ReviewBoard> selectByWriter(String writer) {
        List<ReviewBoard> result = jdbcTemplate.query("select * security.reviewBoard where writer = ?", reviewBoardRowMapper(), writer);
        return result;
    }

    @Override
    public List<ReviewBoard> selectAll() {
        return jdbcTemplate.query("select * from security.reviewBoard", reviewBoardRowMapper());
    }
    @Override
    public boolean incrementViews(Long id) {
        return false;
    }

    @Override
    public boolean updateCommentCnt(Long id, int commentCnt) {
        return false;
    }

    @Override
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    private RowMapper<ReviewBoard> reviewBoardRowMapper(){
        return (rs, rowNum) -> {
            ReviewBoard reviewBoard = new ReviewBoard();
            reviewBoard.setId(rs.getLong("board_id"));
            reviewBoard.setTitle(rs.getString("title"));
            reviewBoard.setContent(rs.getString("content"));
            reviewBoard.setWriter(rs.getString("writer"));
            reviewBoard.setViewCnt(rs.getInt("viewCnt"));
            reviewBoard.setRegisterDate(rs.getTimestamp("regdate"));
            reviewBoard.setUpdateDate(rs.getTimestamp("updateDate"));
            reviewBoard.setDeleteDate(rs.getTimestamp("deleteDate"));
            return reviewBoard;
        };
    }


}
