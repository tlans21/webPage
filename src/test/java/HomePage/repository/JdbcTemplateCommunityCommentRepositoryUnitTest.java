package HomePage.repository;

import HomePage.domain.model.entity.CommunityComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JdbcTemplateCommunityCommentRepositoryUnitTest {
    @Mock
    private JdbcTemplate mockJdbcTemplate;
    private JdbcTemplateCommunityCommentRepository communityCommentRepository;
    private CommunityComment testComment;

    @BeforeEach
    void setUp(){
        communityCommentRepository = new JdbcTemplateCommunityCommentRepository(mockJdbcTemplate);
        ReflectionTestUtils.setField(communityCommentRepository, "tableName", "test.community_comment");

        testComment = new CommunityComment();
        testComment.setId(1L); // 테스트 코멘트 ID는 1로 설정
        testComment.setWriter("testUser");
        testComment.setBoardId(2L); // 게시판 ID는 2로 설정
        testComment.setContent("test Content");
        testComment.setRegisterDate(new Timestamp(System.currentTimeMillis()));
    }

    @Test
    void save() throws SQLException {
        //given : 준비
        when(mockJdbcTemplate.update(
                any(),
                any(KeyHolder.class)
        )).thenAnswer(invocation -> {
            KeyHolder keyHolder = invocation.getArgument(1); // keyHolder 접근
            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put("comment_id", 1L);
            ((GeneratedKeyHolder) keyHolder).getKeyList().add(keyMap);
            return 1;
        });

        // when : 실행
        CommunityComment savedComment = communityCommentRepository.save(testComment);

        // then : 검증
        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getId()).isEqualTo(1L);

        ArgumentCaptor<PreparedStatementCreator> pscCaptor = ArgumentCaptor.forClass(PreparedStatementCreator.class);
        verify(mockJdbcTemplate).update(pscCaptor.capture(), any(KeyHolder.class));
        // PreparedStatementCreator 검증
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPs = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(anyString(), any(String[].class))).thenReturn(mockPs);

        pscCaptor.getValue().createPreparedStatement(mockConnection);

        verify(mockPs).setString(1, testComment.getWriter());
        verify(mockPs).setLong(2, testComment.getBoardId());
        verify(mockPs).setString(3, testComment.getContent());
        verify(mockPs).setTimestamp(4, testComment.getRegisterDate());
        verify(mockPs).setTimestamp(5, testComment.getUpdateDate());
        verify(mockPs).setTimestamp(6, testComment.getDeleteDate());


    }

    @Test
    void countByBoardId() {
        //given : 준비
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE board_id = ?", communityCommentRepository.getTableName());
        Long boardId = 2L;
        int expectedCount = 5;
        when(mockJdbcTemplate.queryForObject(
                eq(sql),
                eq(Integer.class),
                eq(boardId)
        )).thenReturn(expectedCount);


        //when : 실행
        int actualCount = communityCommentRepository.countByBoardId(boardId);
        //then : 검증
        assertThat(actualCount).isEqualTo(expectedCount);
        verify(mockJdbcTemplate).queryForObject(anyString(), eq(Integer.class), eq(boardId));
    }

    @Test
    void selectAll() {
        //given : 준비
        List<CommunityComment> expectedComments = Arrays.asList(testComment);
        String sql = String.format("SELECT * FROM %s", communityCommentRepository.getTableName());
        when(mockJdbcTemplate.query(eq(sql), any(RowMapper.class)))
                .thenReturn(expectedComments);

        //when : 실행
        List<CommunityComment> actualComments = communityCommentRepository.selectAll();

        //then : 검증
        assertThat(actualComments).isEqualTo(expectedComments);
        verify(mockJdbcTemplate).query(eq(sql), any(RowMapper.class));
    }

    @Test
    void update() {
        //given : 준비
        String sql = String.format("UPDATE %s SET content = ?, updateDate = ? WHERE comment_id = ?", communityCommentRepository.getTableName());
        when(mockJdbcTemplate.update(
                eq(sql),
                anyString(),
                any(Timestamp.class),
                eq(testComment.getId())
        )).thenReturn(1);
        //when : 실행
        boolean result = communityCommentRepository.update(testComment);

        //then : 검증
        assertThat(result).isTrue();
        verify(mockJdbcTemplate).update(eq(sql), anyString(), any(Timestamp.class), eq(testComment.getId()));
    }

    @Test
    void findCommentById() {
        //given : 준비
        String sql = String.format("SELECT * FROM %s WHERE comment_id = ?", communityCommentRepository.getTableName());
        when(mockJdbcTemplate.query(
                eq(sql),
                any(RowMapper.class),
                eq(testComment.getId())
        )).thenReturn(Collections.singletonList(testComment));
        //when : 실행
        Optional<CommunityComment> result = communityCommentRepository.findCommentById(testComment.getId());
        //then : 검증
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testComment);
        verify(mockJdbcTemplate).query(anyString(), any(RowMapper.class), eq(result.get().getId()));
    }

    @Test
    void selectByBoardId() {
        //given : 준비
        String sql = String.format("SELECT * FROM %s WHERE board_id = ?", communityCommentRepository.getTableName());
        when(mockJdbcTemplate.query(
                eq(sql),
                any(RowMapper.class),
                eq(testComment.getBoardId())
        )).thenReturn(Arrays.asList(testComment));
        //when : 실행
        List<CommunityComment> result = communityCommentRepository.selectByBoardId(testComment.getBoardId());
        //then : 검증
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testComment);
        verify(mockJdbcTemplate).query(eq(sql), any(RowMapper.class), eq(result.get(0).getBoardId()));
    }

    @Test
    void deleteByWriter() {
        //given : 준비
        String sql = String.format("DELETE FROM %s WHERE writer = ?", communityCommentRepository.getTableName());
        when(mockJdbcTemplate.update(
                eq(sql),
                eq(testComment.getWriter())
        )).thenReturn(1);
        //when : 실행
        boolean result = communityCommentRepository.deleteByWriter(testComment.getWriter());
        //then : 검증
        assertThat(result).isTrue();
        verify(mockJdbcTemplate).update(eq(sql), eq(testComment.getWriter()));
    }

    @Test
    void deleteByCommentId() {
        //given : 준비
        String sql = String.format("DELETE FROM %s WHERE comment_id = ?", communityCommentRepository.getTableName());
        when(mockJdbcTemplate.update(
                eq(sql),
                eq(testComment.getId())
        )).thenReturn(1);
        //when : 실행
        boolean result = communityCommentRepository.deleteByCommentId(testComment.getId());
        //then : 검증
        assertThat(result).isTrue();
        verify(mockJdbcTemplate).update(eq(sql), eq(testComment.getId()));
    }

    @Test
    void deleteByBoardId() {
        //given : 준비
        String sql = String.format("DELETE FROM %s WHERE board_id = ?", communityCommentRepository.getTableName());
        when(mockJdbcTemplate.update(
                eq(sql),
                eq(testComment.getBoardId())
        )).thenReturn(1);
        //when : 실행
        boolean result = communityCommentRepository.deleteByBoardId(testComment.getBoardId());
        //then : 검증
        assertThat(result).isTrue();
        verify(mockJdbcTemplate).update(eq(sql), eq(testComment.getBoardId()));
    }
}