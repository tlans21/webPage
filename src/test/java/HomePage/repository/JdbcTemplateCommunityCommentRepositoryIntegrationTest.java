package HomePage.repository;

import HomePage.domain.model.CommunityBoard;
import HomePage.domain.model.CommunityComment;
import HomePage.domain.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class JdbcTemplateCommunityCommentRepositoryIntegrationTest {
    @Autowired
    private JdbcTemplateCommunityCommentRepository commentRepository;
    @Autowired
    private JdbcTemplateCommunityBoardRepository boardRepository;
    @Autowired
    private JdbcTemplateUserRepository userRepository;
    @Autowired
    DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private CommunityComment testComment;
    private CommunityBoard testBoard;

    @BeforeEach
    void setUp() {
        // 테스트 시작 전 테이블 비우기
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        userRepository.setTableName("test.user");
        commentRepository.setTableName("test.community_comment");
        boardRepository.setTableName("test.community_board");
        cleanUpDatabase();

        // when : 사용자 준비
        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setEmail("test@test.com");
        testUser.setPassword("testPassword");
        testUser.setRoles("ROLE_USER");
        userRepository.save(testUser);

        User commentUser = new User();
        commentUser.setUsername("commentUser");
        commentUser.setEmail("comment@comment.com");
        commentUser.setPassword("commentPassword");
        commentUser.setRoles("ROLE_USER");
        userRepository.save(commentUser);

        // when : 게시판 준비
        testBoard = new CommunityBoard();
        testBoard.setWriter("testUser");
        testBoard.setTitle("Test Board");
        testBoard.setContent("Test Content");
        testBoard = boardRepository.save(testBoard);

        // when : 댓글 준비
        testComment = new CommunityComment();
        testComment.setBoard_id(testBoard.getId());
        testComment.setWriter("commentUser");
        testComment.setContent("Test Comment");
    }

    @AfterEach
    void tearDown(){
        // 테스트 시작 후 테이블 비우기
        cleanUpDatabase();
    }
    private void cleanUpDatabase() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("SET SQL_SAFE_UPDATES = 0");
        try {
            jdbcTemplate.execute("DELETE FROM test.user WHERE id > 1");
            jdbcTemplate.execute("DELETE FROM test.community_comment WHERE comment_id > 1");
            jdbcTemplate.execute("DELETE FROM test.community_board WHERE board_id > 1");
        } finally {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
            jdbcTemplate.execute("SET SQL_SAFE_UPDATES = 1");
        }
    }
        @Test
        void save() {
            // when : 실행
            CommunityComment savedComment = commentRepository.save(testComment);

            // then : 검증
            assertNotNull(savedComment.getId());
            assertEquals(testComment.getBoard_id(), savedComment.getBoard_id());
            assertEquals(testComment.getWriter(), savedComment.getWriter());
            assertEquals(testComment.getContent(), savedComment.getContent());
        }

    @Test
    void countByBoardId() {
        //given : 준비
        CommunityComment savedComment = commentRepository.save(testComment);
        //when : 실행
        int result = commentRepository.countByBoardId(savedComment.getBoard_id());
        //then : 검증
        assertThat(result).isEqualTo(1);
    }

    @Test
    void selectAll() {
        //given : 준비
        CommunityComment savedComment = commentRepository.save(testComment);
        //when : 실행
        List<CommunityComment> communityComments = commentRepository.selectAll();
        //then : 검증
        assertThat(communityComments).isNotNull();
        assertThat(communityComments).hasSize(1);
        assertThat(communityComments).extracting("Writer").containsExactlyInAnyOrder("commentUser");
    }

    @Test
    void update() {
        //given : 준비
        CommunityComment savedComment = commentRepository.save(testComment);
        String updatedContent = "Updated Content";
        savedComment.setContent(updatedContent);
        //when : 실행
        boolean result = commentRepository.update(savedComment);
        //then : 검증
        assertThat(result).isTrue();
        // 업데이트된 댓글 재조회
        CommunityComment updatedComment = commentRepository.findCommentById(savedComment.getId()).orElse(null);
        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getContent()).isEqualTo(updatedContent);
        assertThat(updatedComment.getWriter()).isEqualTo(savedComment.getWriter());
        assertThat(updatedComment.getBoard_id()).isEqualTo(savedComment.getBoard_id());
    }

    @Test
    void findCommentById() {
        //given : 준비
        CommunityComment savedComment = commentRepository.save(testComment);
        //when : 실행
        CommunityComment foundComment = commentRepository.findCommentById(savedComment.getId()).orElse(null);
        //then : 검증
        assertThat(foundComment).isNotNull();
        assertThat(foundComment.getContent()).isEqualTo(savedComment.getContent());
        assertThat(foundComment.getWriter()).isEqualTo(savedComment.getWriter());
        assertThat(foundComment.getBoard_id()).isEqualTo(savedComment.getBoard_id());
    }

    @Test
    void selectByBoardId() {
        //given : 준비
        CommunityComment savedComment = commentRepository.save(testComment);
        //when : 실행
        List<CommunityComment> communityComments = commentRepository.selectByBoardId(testBoard.getId());
        //then : 검증
        assertThat(communityComments).isNotEmpty();
        assertThat(communityComments.get(0).getWriter()).isEqualTo(savedComment.getWriter());
        assertThat(communityComments.get(0).getContent()).isEqualTo(savedComment.getContent());
        assertThat(communityComments.get(0).getBoard_id()).isEqualTo(savedComment.getBoard_id());
    }

    @Test
    void deleteByWriter() {
        //given : 준비
        CommunityComment savedComment = commentRepository.save(testComment);
        //when : 실행
        boolean result = commentRepository.deleteByWriter(savedComment.getWriter());
        //then : 검증
        assertThat(result).isTrue();
    }

    @Test
    void deleteByCommentId() {
        //given : 준비
        CommunityComment savedComment = commentRepository.save(testComment);
        //when : 실행
        boolean result = commentRepository.deleteByCommentId(savedComment.getId());
        //then : 검증
        assertThat(result).isTrue();
    }

    @Test
    void deleteByBoardId() {
        // given:  준비
        // 게시판은 beforeEach 어노테이션에서의 setUp메서드 만들어져있음.
        CommunityComment savedComment = commentRepository.save(testComment);
        //when : 실행
        boolean result = commentRepository.deleteByBoardId(testBoard.getId());
        //then : 검증
        assertThat(result).isTrue();
    }
    @Test
    void update_NonExistingComment() {
        //given : 준비
        CommunityComment nonExistingComment = new CommunityComment();
        nonExistingComment.setId(9999L); // 존재하지 않는 ID
        nonExistingComment.setContent("Updated Content");
        //when : 실행
        boolean result = commentRepository.update(nonExistingComment);
        //then : 검증
        assertThat(result).isFalse();
    }

    @Test
    void findCommentById_NonExisting() {
        //when : 실행
        Optional<CommunityComment> foundComment = commentRepository.findCommentById(9999L);
        //then : 검증
        assertThat(foundComment).isEmpty();
    }

    @Test
    void selectByBoardId_NonExisting() {
        //when : 실행
        List<CommunityComment> communityComments = commentRepository.selectByBoardId(9999L);
        //then : 검증
        assertThat(communityComments).isEmpty();
    }

    @Test
    void deleteByWriter_NonExisting() {
        //when : 실행
        boolean result = commentRepository.deleteByWriter("nonExistingWriter");
        //then : 검증
        assertThat(result).isFalse();
    }

    @Test
    void deleteByCommentId_NonExisting() {
        //when : 실행
        boolean result = commentRepository.deleteByCommentId(9999L);
        //then : 검증
        assertThat(result).isFalse();
    }


    @Test
    void save_WithInvalidData() {
        // given : 준비
        CommunityComment invalidComment = new CommunityComment();
        // board_id를 설정하지 않음
        invalidComment.setWriter("testWriter");
        invalidComment.setContent("Test Content");

        // when & then : 실행 및 검증
        assertThatThrownBy(() -> commentRepository.save(invalidComment))
            .isInstanceOf(Exception.class);  // 예외 타입은 실제 구현에 따라 조정 필요
    }

    @Test
    void countByBoardId_NonExisting() {
        //when : 실행
        int result = commentRepository.countByBoardId(9999L);
        //then : 검증
        assertThat(result).isEqualTo(0);
    }
}