package HomePage.repository;

import HomePage.domain.model.CommunityComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        testComment.setBoard_id(2L); // 게시판 ID는 2로 설정
        testComment.setContent("test Content");
        testComment.setRegisterDate(new Timestamp(System.currentTimeMillis()));
    }

    @Test
    void save() {
        //given : 준비
        when(mockJdbcTemplate.update(
                anyString(),
                any(MapSqlParameterSource.class),
                any(KeyHolder.class),
                eq(new String[]{"comment_id"})
        )).thenAnswer(invocation -> {
            KeyHolder keyHolder = invocation.getArgument(2);
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

       verify(mockJdbcTemplate).update(
               anyString(),
               any(MapSqlParameterSource.class),
               any(KeyHolder.class),
               eq(new String[]{"comment_id"})
       );

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
    void selectById() {
        //given : 준비
        String sql = String.format("SELECT * FROM %s WHERE board_id = ?", communityCommentRepository.getTableName());
        when(mockJdbcTemplate.query(
                eq(sql),
                any(RowMapper.class),
                eq(testComment.getBoard_id())
        )).thenReturn(Arrays.asList(testComment));
        //when : 실행
        List<CommunityComment> result = communityCommentRepository.selectById(testComment.getBoard_id());
        //then : 검증
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testComment);
        verify(mockJdbcTemplate).query(eq(sql), any(RowMapper.class), eq(result.get(0).getBoard_id()));
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
                eq(testComment.getBoard_id())
        )).thenReturn(1);
        //when : 실행
        boolean result = communityCommentRepository.deleteByBoardId(testComment.getBoard_id());
        //then : 검증
        assertThat(result).isTrue();
        verify(mockJdbcTemplate).update(eq(sql), eq(testComment.getBoard_id()));
    }
}