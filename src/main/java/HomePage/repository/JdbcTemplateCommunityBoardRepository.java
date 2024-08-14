package HomePage.repository;

import HomePage.domain.model.CommunityBoard;
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

public class JdbcTemplateCommunityBoardRepository implements BoardRepository<CommunityBoard> {
    private final JdbcTemplate jdbcTemplate;
    private String tableName = "security.communityboard"; // 기본 테이블 이름;
    public JdbcTemplateCommunityBoardRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public CommunityBoard save(CommunityBoard communityBoard) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("communityboard").usingGeneratedKeyColumns("board_id");
        Timestamp regDate = new Timestamp(System.currentTimeMillis());
        communityBoard.setRegisterDate(regDate);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("writer", communityBoard.getWriter());
        parameters.put("title", communityBoard.getTitle());
        parameters.put("content", communityBoard.getContent());
        parameters.put("regdate", communityBoard.getRegisterDate());
        parameters.put("viewCnt", communityBoard.getViewCnt());

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        communityBoard.setId(key.longValue());
        return communityBoard;
    }

    @Override
    public List<CommunityBoard> findPage(int offset, int limit) {
        String sql = "SELECT * FROM security.communityboard WHERE deleteDate IS NULL ORDER BY regdate DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, communityBoardRowMapper(), limit, offset); // offset은 건너띄려는 행, limit는 조회하려는 갯수
    }
    @Override
    public List<CommunityBoard> findPageOrderByTopView(int offset, int limit) {
        String sql = "SELECT * FROM security.communityboard ORDER BY viewCnt DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, communityBoardRowMapper(), limit, offset);
    }

    @Override
    public List<CommunityBoard> findPageOrderByTopCommentCnt(int offset, int limit) {
        String sql = "SELECT * FROM security.communityboard ORDER BY commentCnt DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, communityBoardRowMapper(), limit, offset);
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM security.communityboard WHERE deleteDate IS NULL";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public boolean update(CommunityBoard communityBoard) {
        String sql = "UPDATE security.communityboard SET title = ?, content = ?, updateDate = ? WHERE board_id = ?";
        Timestamp updateDate = new Timestamp(System.currentTimeMillis());
        int isUpdate = jdbcTemplate.update(sql, communityBoard.getTitle(), communityBoard.getContent(), updateDate, communityBoard.getId());
        if (isUpdate == 0 || isUpdate > 1){
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteById(Long id) {
        int isDelete = jdbcTemplate.update("DELETE FROM security.communityboard WHERE board_id = ?", id);
        return isDelete == 1;
    }

    @Override
    public Optional<CommunityBoard> selectById(Long id) {
        List<CommunityBoard> result = jdbcTemplate.query("select * from security.communityboard where board_id = ?", communityBoardRowMapper(), id);
        return result.stream().findAny();
    }

    @Override
    public Optional<CommunityBoard> selectByTitle(String title) {
        List<CommunityBoard> result = jdbcTemplate.query("select * from security.communityboard where title = ?", communityBoardRowMapper(), title);
        return result.stream().findAny();
    }

    @Override
    public Optional<CommunityBoard> selectByWriter(String writer) {
        List<CommunityBoard> result = jdbcTemplate.query("select * from security.communityboard where writer = ?", communityBoardRowMapper(), writer);
        return result.stream().findAny();
    }

    @Override
    public List<CommunityBoard> selectAll() {
        return jdbcTemplate.query("select * from security.communityboard", communityBoardRowMapper());
    }
    @Override
    public int incrementViews(Long id){
        return jdbcTemplate.update("UPDATE security.communityboard SET viewCnt = viewCnt + 1 where board_id = ?", id);
    }
    public int updateCommentCnt(Long id, int commentCnt){
        return jdbcTemplate.update("UPDATE security.communityboard SET commentCnt = ? where board_id = ?", commentCnt, id);
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
            communityBoard.setRegisterDate(rs.getTimestamp("regdate"));
            communityBoard.setUpdateDate(rs.getTimestamp("updateDate"));
            communityBoard.setDeleteDate(rs.getTimestamp("deleteDate"));
            communityBoard.setCommentCnt(rs.getInt("commentCnt"));
            return communityBoard;
        };
    }
}
