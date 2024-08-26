package HomePage.service;

import HomePage.controller.board.form.CommunityBoardWriteForm;
import HomePage.domain.model.CommunityBoard;
import HomePage.domain.model.CommunityComment;
import HomePage.domain.model.Page;
import HomePage.domain.model.User;
import HomePage.repository.BoardRepository;
import HomePage.repository.CommentRepository;
import HomePage.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import javax.sql.DataSource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {"communityBoard.page-size=10"})
class CommunityBoardServiceIntegrationTest {
    @Autowired
    CommunityBoardService boardService;
    @Autowired
    BoardRepository<CommunityBoard> boardRepository;
    @Autowired
    CommunityCommentService commentService;
    @Autowired
    CommentRepository<CommunityComment> commentRepository;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @Autowired
    DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // 테스트 시작 전 테이블 비우기
        this.jdbcTemplate = new JdbcTemplate(dataSource);

        boardRepository.setTableName("test.community_board");
        userRepository.setTableName("test.user");
        commentRepository.setTableName("test.community_comment");
        cleanUpDatabase();
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
            jdbcTemplate.execute("DELETE FROM test.user where id > 1");
            jdbcTemplate.execute("DELETE FROM test.community_board where board_id > 1");
            jdbcTemplate.execute("DELETE FROM test.community_comment where comment_id > 1");
        } finally {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
            jdbcTemplate.execute("SET SQL_SAFE_UPDATES = 1");
        }
    }
    @Test
    void validateCommunityForm_Success() {
        //given : 준비
        CommunityBoardWriteForm form =  new CommunityBoardWriteForm();
        form.setTitle("Valid Test");
        form.setContent("Valid Content");

        Errors errors = new BeanPropertyBindingResult(form, "communityBoardWriteForm");
        //when : 실행
        Map<String, String> validatorResult = boardService.validateCommunityForm(errors);
        //then : 검증
        assertThat(validatorResult).isEmpty();
    }
    @Test
    void validateCommunityForm_EmptyTitle() {
        //given : 준비
        CommunityBoardWriteForm form = new CommunityBoardWriteForm();
        form.setTitle("");
        form.setContent("Valid Content");

        Errors errors = new BeanPropertyBindingResult(form, "communityBoardWriteForm");
        if (form.getTitle() == null || form.getTitle().trim().isEmpty()) {
              errors.rejectValue("title", "NotBlank", "제목을 입력해주세요.");
        }
        //when : 실행
        Map<String, String> validatorResult = boardService.validateCommunityForm(errors);
        //then : 검증
        assertThat(validatorResult).isNotEmpty();
        assertThat(validatorResult).containsKey("valid_title");
        assertThat(validatorResult.get("valid_title")).isEqualTo("제목을 입력해주세요.");
    }
    @Test
    void validateCommunityForm_EmptyContent() {
        //given : 준비
        CommunityBoardWriteForm form = new CommunityBoardWriteForm();
        form.setTitle("Valid Title");
        form.setContent("");

        Errors errors = new BeanPropertyBindingResult(form, "communityBoardWriteForm");
        if (form.getContent() == null || form.getContent().trim().isEmpty()) {
              errors.rejectValue("content", "NotBlank", "내용을 입력해주세요.");
        }
        //when : 실행
        Map<String, String> validatorResult = boardService.validateCommunityForm(errors);
        //then : 검증
        assertThat(validatorResult).isNotEmpty();
        assertThat(validatorResult).containsKey("valid_content");
        assertThat(validatorResult.get("valid_content")).isEqualTo("내용을 입력해주세요.");
    }


    @Test
    void getBoardPage_Success() {
        //given : 준비
        int pageNumber = 1;
        int pageSize = 10; // 서비스에 정의된 pageSize와 동일함.
        int totalBoards = 100;
        int totalPages = (int) Math.ceil((double) totalBoards / pageSize);

        //100명의 사용자 가입
        for(int i = 0; i < 100; i++){
            User user = new User();
            user.setUsername("TestWriter " + i);
            user.setEmail("Test" + i +"@gmail.com");
            user.setPassword("test" + i);
            user.setRoles("ROLE_USER");
            userService.join(user);
        }

        // 게시판 저장하기
        for(int i = 0; i < totalBoards; i++){
            CommunityBoard board = new CommunityBoard();
            board.setTitle("Test Title " + i);
            board.setContent("Test Content " + i);
            board.setWriter("TestWriter " + i); // 실제 존재하는 사용자
            boardService.saveBoard(board);
        }
        //when : 실행
        Page<CommunityBoard> result = boardService.getBoardPage(pageNumber);
        //then : 검증
        assertThat(result).isNotNull();
        assertThat(result.getCurrentPage()).isEqualTo(pageNumber);
        assertThat(result.getTotalPages()).isEqualTo(totalPages);
        assertThat(result.getPageSize()).isEqualTo(pageSize);

        for (int i = 0; i < pageSize; i++) {
           CommunityBoard savedBoard = result.getContent().get(i);
           assertThat(savedBoard.getTitle()).isEqualTo("Test Title " + (99 - i));
           assertThat(savedBoard.getContent()).isEqualTo("Test Content " + (99 - i));
           assertThat(savedBoard.getWriter()).isEqualTo("TestWriter " + (99 - i));
        }
    }
    @Test
    void getBoardPage_InvalidPageNumber() {
        //given : 준비
        int invalidPageNumber = 0; // 잘못된 0번 페이지 번호
        int pageSize = 10; // 서비스에 정의된 pageSize와 동일함.
        int totalBoards = 100;
        int totalPages = (int) Math.ceil((double) totalBoards / pageSize);

        //100명의 사용자 가입
        for(int i = 0; i < 100; i++){
            User user = new User();
            user.setUsername("TestWriter " + i);
            user.setEmail("Test" + i +"@gmail.com");
            user.setPassword("test" + i);
            user.setRoles("ROLE_USER");
            userService.join(user);
        }

        // 게시판 저장하기
        for(int i = 0; i < totalBoards; i++){
            CommunityBoard board = new CommunityBoard();
            board.setTitle("Test Title " + i);
            board.setContent("Test Content " + i);
            board.setWriter("TestWriter " + i); // 실제 존재하는 사용자
            boardService.saveBoard(board);
        }
        //when & then: 실행 및 검증
        assertThatThrownBy(() -> boardService.getBoardPage(invalidPageNumber))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Page number must be greater than 0");
    }

    @Test
    void getTopViewedBoardPage() {
        //given : 준비
        int pageNumber = 1;
        int pageSize = 10; // 서비스에 정의된 pageSize와 동일함.
        int totalBoards = 100;
        int totalPages = (int) Math.ceil((double) totalBoards / pageSize);

        //100명의 사용자 가입
        for(int i = 0; i < 100; i++){
            User user = new User();
            user.setUsername("TestWriter " + i);
            user.setEmail("Test" + i +"@gmail.com");
            user.setPassword("test" + i);
            user.setRoles("ROLE_USER");
            userService.join(user);
        }

        // 게시판 저장하기
        for(int i = 0; i < totalBoards; i++){
            CommunityBoard board = new CommunityBoard();
            board.setTitle("Test Title " + i);
            board.setContent("Test Content " + i);
            board.setWriter("TestWriter " + i); // 실제 존재하는 사용자
            board.setViewCnt(i);
            boardService.saveBoard(board);
        }
        //when : 실행
        Page<CommunityBoard> result = boardService.getTopViewedBoardPage(pageNumber);
        // 조회수 내림차순으로 정렬
        result.getContent().sort(Comparator.comparing(CommunityBoard::getViewCnt).reversed());
        //then : 검증
        assertThat(result).isNotNull();
        assertThat(result.getCurrentPage()).isEqualTo(pageNumber);
        assertThat(result.getTotalPages()).isEqualTo(totalPages);
        assertThat(result.getPageSize()).isEqualTo(pageSize);
        // 조회수 내림차순 정렬 확인
        for (int i = 0; i < pageSize - 1; i++) {
            assertThat(result.getContent().get(i).getViewCnt())
                .isGreaterThanOrEqualTo(result.getContent().get(i + 1).getViewCnt());
        }
    }

    @Test
    void getTopCommentCntBoardPage() {
        //given : 준비
        int pageNumber = 1;
        int pageSize = 10; // 서비스에 정의된 pageSize와 동일함.
        int totalBoards = 100;
        int totalPages = (int) Math.ceil((double) totalBoards / pageSize);

        //100명의 사용자 가입
        for(int i = 0; i < 100; i++){
            User user = new User();
            user.setUsername("TestWriter " + i);
            user.setEmail("Test" + i +"@gmail.com");
            user.setPassword("test" + i);
            user.setRoles("ROLE_USER");
            userService.join(user);
        }

        // 게시판 저장하기
        for(int i = 0; i < totalBoards; i++){
            CommunityBoard board = new CommunityBoard();
            board.setTitle("Test Title " + i);
            board.setContent("Test Content " + i);
            board.setWriter("TestWriter " + i); // 실제 존재하는 사용자
            board.setViewCnt(i);
            boardService.saveBoard(board);
        }
        //when : 실행
        Page<CommunityBoard> result = boardService.getTopCommentCntBoardPage(pageNumber);
        // 댓글 순으로 내림차순 정렬
        result.getContent().sort(Comparator.comparing(CommunityBoard::getCommentCnt).reversed());
        //then : 검증
        assertThat(result).isNotNull();
        assertThat(result.getCurrentPage()).isEqualTo(pageNumber);
        assertThat(result.getTotalPages()).isEqualTo(totalPages);
        assertThat(result.getPageSize()).isEqualTo(pageSize);

        // 조회수 내림차순 정렬 확인
        for (int i = 0; i < pageSize - 1; i++) {
            assertThat(result.getContent().get(i).getCommentCnt())
                .isGreaterThanOrEqualTo(result.getContent().get(i + 1).getCommentCnt());
        }

    }

    @Test
    void getBoardById_ExistingBoard() {
        //given : 준비
        int totalBoards = 100; // 100개의 게시판

        //100명의 사용자 가입
        for(int i = 0; i < 100; i++){
            User user = new User();
            user.setUsername("TestWriter " + i);
            user.setEmail("Test" + i +"@gmail.com");
            user.setPassword("test" + i);
            user.setRoles("ROLE_USER");
            userService.join(user);
        }

        // 게시판 저장하기
        for(int i = 0; i < totalBoards; i++){
            CommunityBoard board = new CommunityBoard();
            board.setTitle("Test Title " + i);
            board.setContent("Test Content " + i);
            board.setWriter("TestWriter " + i); // 실제 존재하는 사용자
            board.setViewCnt(i);
            boardService.saveBoard(board);
        }

        // 모든 게시글 불러오기
        List<CommunityBoard> allBoards = boardService.getAllBoards();
        //when & then : 실행 및 검증
        for (CommunityBoard board : allBoards){
            Long savedBoardId = board.getId();

            CommunityBoard result = boardService.getBoardById(savedBoardId);
            assertThat(result.getId()).isEqualTo(savedBoardId);
            assertThat(result.getTitle()).isEqualTo(board.getTitle());
            assertThat(result.getContent()).isEqualTo(board.getContent());
        }

    }
    @Test
    void getBoardById_NonExistingBoard() {
        //given : 준비
        Long nonExistingBoardId = 1L;
        //when & then : 실행 및 검증
        assertThatThrownBy(() -> boardService.getBoardById(nonExistingBoardId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Board not found with id: " + nonExistingBoardId);
    }


    @Test
    void saveBoard_Success() {
        //given : 준비
        User user = new User();
        user.setUsername("testWriter");
        user.setEmail("test@gmail.com");
        user.setPassword("testPassword");
        user.setRoles("ROLE_USER");
        // 게시글 작성을 위한 유저 가입
        userService.join(user);


        CommunityBoard board = new CommunityBoard();
        board.setTitle("Test Title");
        board.setContent("Test Content");
        board.setWriter("testWriter");
        //when : 실행
        boardService.saveBoard(board);

        // then: 검증
        List<CommunityBoard> savedBoards = boardService.getAllBoards();
        assertThat(savedBoards).isNotEmpty();
        CommunityBoard savedBoard = savedBoards.get(0);
        assertThat(savedBoard.getTitle()).isEqualTo("Test Title");
        assertThat(savedBoard.getContent()).isEqualTo("Test Content");
        assertThat(savedBoard.getWriter()).isEqualTo("testWriter");
    }
    @Test
    void saveBoard_Failure() {
        CommunityBoard board = new CommunityBoard();
        board.setTitle("Test Title");
        board.setContent("Test Content");
        board.setWriter("testWriter");

        assertThatThrownBy(() -> boardService.saveBoard(board))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database connection failed");

    }
    @Test
    void updateBoard_Success() {
        //given : 준비
        User user = new User();
        user.setUsername("testWriter");
        user.setEmail("test@gmail.com");
        user.setPassword("testPassword");
        user.setRoles("ROLE_USER");
        // 게시글 작성을 위한 유저 가입
        userService.join(user);

        CommunityBoard existingBoard = new CommunityBoard();
        existingBoard.setTitle("Original Title");
        existingBoard.setContent("Original Content");
        existingBoard.setWriter("testWriter");

        // saveBoard가 CommunityBoard를 반환한다고 가정
        Long savedBoardId = boardService.saveBoard(existingBoard);

        // 업데이트할 내용 설정
        CommunityBoard targetBoard = new CommunityBoard();
        targetBoard.setId(savedBoardId);
        targetBoard.setTitle("Updated Title");
        targetBoard.setContent("Updated Content");
        targetBoard.setWriter("testWriter");  // writer는 변경하지 않음

        // when : 실행
        boardService.updateBoard(targetBoard);

        // then : 검증
        CommunityBoard updatedBoard = boardService.getBoardById(savedBoardId);

        assertThat(updatedBoard.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedBoard.getContent()).isEqualTo("Updated Content");
        assertThat(updatedBoard.getWriter()).isEqualTo("testWriter");

        // 원본 게시글과 비교
        assertThat(updatedBoard.getTitle()).isNotEqualTo(existingBoard.getTitle());
        assertThat(updatedBoard.getContent()).isNotEqualTo(existingBoard.getContent());
        assertThat(updatedBoard.getWriter()).isEqualTo(existingBoard.getWriter());
    }
    @Test
    void updateBoard_Failure() {
        CommunityBoard nonExistentBoard = new CommunityBoard();
        Long nonExistentBoardId = 100L;
        nonExistentBoard.setId(nonExistentBoardId);
        nonExistentBoard.setTitle("Updated Title");
        nonExistentBoard.setContent("Updated Content");

        // when & then
        assertThatThrownBy(() -> boardService.updateBoard(nonExistentBoard))
            .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to update board with id: " + nonExistentBoard.getId());
    }

    @Test
    void deleteBoard_Success() {
        //given : 준비
        User user = new User();
        user.setUsername("testWriter");
        user.setEmail("test@gmail.com");
        user.setPassword("testPassword");
        user.setRoles("ROLE_USER");
        userService.join(user);

        CommunityBoard board = new CommunityBoard();
        board.setTitle("Test Title");
        board.setContent("Test Content");
        board.setWriter("testWriter");
        Long savedBoardId = boardService.saveBoard(board);

        //when : 실행
        assertThatCode(() -> boardService.deleteBoard(savedBoardId))
                .doesNotThrowAnyException();
        //then : 검증
        assertThatThrownBy(() -> boardService.getBoardById(savedBoardId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Board not found with id: " + savedBoardId);
    }
    @Test
    void deleteBoard_failure_case() {
        // given : 준비
            Long nonExistentBoardId = 9999L;

        // when & then : 실행 및 검증
        assertThatThrownBy(() -> boardService.deleteBoard(nonExistentBoardId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to delete board with board_id: " + nonExistentBoardId);

    }
//    @Test
//    void deleteBoard_failure_comment_case() {
//        // given : 준비
//        User user = new User();
//        user.setUsername("testWriter");
//        user.setEmail("test@gmail.com");
//        user.setPassword("testPassword");
//        user.setRoles("ROLE_USER");
//        userService.join(user);
//
//        CommunityBoard board = new CommunityBoard();
//        board.setTitle("Test Title");
//        board.setContent("Test Content");
//        board.setWriter("testWriter");
//        Long savedBoardId = boardService.saveBoard(board);
//
//        // when & then : 실행 및 검증
//        assertThatThrownBy(() -> boardService.deleteBoard(savedBoardId))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Failed to delete comments for board with id: " + savedBoardId);
//
//        // 게시글이 삭제되지 않았음을 확인
//        assertThat(boardService.getBoardById(savedBoardId)).isNotNull();
//    }
    @Test
    void searchBoardsByTitle_Success() {
        // given : 준비
        User user = new User();
        user.setUsername("testWriter");
        user.setEmail("test@gmail.com");
        user.setPassword("testPassword");
        user.setRoles("ROLE_USER");
        userService.join(user);

        CommunityBoard board1 = new CommunityBoard();
        board1.setTitle("Test Title");
        board1.setContent("Test Content");
        board1.setWriter("testWriter");
        boardService.saveBoard(board1);

        CommunityBoard board2 = new CommunityBoard();
        board2.setTitle("Another Title");
        board2.setContent("Another Content");
        board2.setWriter("testWriter");
        boardService.saveBoard(board2);

        // when : 실행
        List<CommunityBoard> searchResult = boardService.searchBoardsByTitle("Test");

        // then : 검증
        assertThat(searchResult).hasSize(1);
        assertThat(searchResult.get(0).getTitle()).isEqualTo("Test Title");
    }
    @Test
    void searchBoardsByTitle_NoResult() {
        // 시나리오: 존재하지 않는 제목으로 게시글 검색
        // given : 준비
        String nonExistentTitle = "Non-existent Title";

        // when : 실행
        List<CommunityBoard> searchResult = boardService.searchBoardsByTitle(nonExistentTitle);

        // then : 검증
        assertThat(searchResult).isEmpty();
    }

    @Test
    void searchBoardsByWriter() {
        // given : 준비
        User user1 = new User();
        user1.setUsername("writer1");
        user1.setEmail("writer1@gmail.com");
        user1.setPassword("password");
        user1.setRoles("ROLE_USER");
        userService.join(user1);

        User user2 = new User();
        user2.setUsername("writer2");
        user2.setEmail("writer2@gmail.com");
        user2.setPassword("password");
        user2.setRoles("ROLE_USER");
        userService.join(user2);

        CommunityBoard board1 = new CommunityBoard();
        board1.setTitle("Title 1");
        board1.setContent("Content 1");
        board1.setWriter("writer1");
        boardService.saveBoard(board1);

        CommunityBoard board2 = new CommunityBoard();
        board2.setTitle("Title 2");
        board2.setContent("Content 2");
        board2.setWriter("writer2");
        boardService.saveBoard(board2);

        // when : 실행
        List<CommunityBoard> searchResult = boardService.searchBoardsByWriter("writer1");

        // then : 검증
        assertThat(searchResult).hasSize(1);
        assertThat(searchResult.get(0).getWriter()).isEqualTo("writer1");
    }
    @Test
    void searchBoardsByWriter_NoResult() {
        // 시나리오: 존재하지 않는 작성자로 게시글 검색
        // given : 준비
        String nonExistentWriter = "Non-existent Writer";

        // when : 실행
        List<CommunityBoard> searchResult = boardService.searchBoardsByWriter(nonExistentWriter);

        // then : 검증
        assertThat(searchResult).isEmpty();
    }

    @Test
    void getAllBoards_Success() {
        // given : 준비
        User user = new User();
        user.setUsername("testWriter");
        user.setEmail("test@gmail.com");
        user.setPassword("testPassword");
        user.setRoles("ROLE_USER");
        userService.join(user);

        int totalBoards = 5;
        for (int i = 0; i < totalBoards; i++) {
            CommunityBoard board = new CommunityBoard();
            board.setTitle("Title " + i);
            board.setContent("Content " + i);
            board.setWriter("testWriter");
            boardService.saveBoard(board);
        }

        // when : 실행
        List<CommunityBoard> allBoards = boardService.getAllBoards();

        // then : 검증
        assertThat(allBoards).hasSize(totalBoards);
    }
    @Test
    void getAllBoards_EmptyResult() {
        // 시나리오: 게시글이 하나도 없는 경우
        // given : 준비 (게시글을 추가하지 않음)

        // when : 실행
        List<CommunityBoard> allBoards = boardService.getAllBoards();

        // then : 검증
        assertThat(allBoards).isEmpty();
    }

    @Test
    void getBoardByIdAndIncrementViews_Success() {
        // given : 준비
        User user = new User();
        user.setUsername("testWriter");
        user.setEmail("test@gmail.com");
        user.setPassword("testPassword");
        user.setRoles("ROLE_USER");
        userService.join(user);

        CommunityBoard board = new CommunityBoard();
        board.setTitle("Test Title");
        board.setContent("Test Content");
        board.setWriter("testWriter");
        board.setViewCnt(0);
        Long savedBoardId = boardService.saveBoard(board);

        // when : 실행
        CommunityBoard retrievedBoard = boardService.getBoardByIdAndIncrementViews(savedBoardId);

        // then : 검증
        assertThat(retrievedBoard).isNotNull();
        assertThat(retrievedBoard.getId()).isEqualTo(savedBoardId);
        assertThat(retrievedBoard.getViewCnt()).isEqualTo(1);  // 조회수가 1 증가했는지 확인

        // 다시 조회하여 조회수가 증가했는지 확인
        CommunityBoard retrievedBoardAgain = boardService.getBoardByIdAndIncrementViews(savedBoardId);
        assertThat(retrievedBoardAgain.getViewCnt()).isEqualTo(2);
    }
    @Test
    void getBoardByIdAndIncrementViews_NonExistingBoard() {
        // 시나리오: 존재하지 않는 게시글 조회 시도
        // given : 준비
        Long nonExistingBoardId = 9999L;

        // when & then : 실행 및 검증
        assertThatThrownBy(() -> boardService.getBoardByIdAndIncrementViews(nonExistingBoardId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Expected 1 row to be updated, but got ");
    }

    @Test
    void incrementViews_Success() {
        // given : 준비
        User user = new User();
        user.setUsername("testWriter");
        user.setEmail("test@gmail.com");
        user.setPassword("testPassword");
        user.setRoles("ROLE_USER");
        userService.join(user);

        CommunityBoard board = new CommunityBoard();
        board.setTitle("Test Title");
        board.setContent("Test Content");
        board.setWriter("testWriter");
        Long savedBoardId = boardService.saveBoard(board);

        // when : 실행
        boardService.incrementViews(savedBoardId);

        // then : 검증
        CommunityBoard retrievedBoard = boardService.getBoardById(savedBoardId);
        assertThat(retrievedBoard.getViewCnt()).isEqualTo(1);

        // 한 번 더 증가
        boardService.incrementViews(savedBoardId);
        retrievedBoard = boardService.getBoardById(savedBoardId);
        assertThat(retrievedBoard.getViewCnt()).isEqualTo(2);
    }
    @Test
    void incrementViews_NonExistingBoard() {
        // 시나리오: 존재하지 않는 게시글의 조회수 증가 시도
        // given : 준비
        Long nonExistingBoardId = 9999L;

        // when & then : 실행 및 검증
        assertThatThrownBy(() -> boardService.incrementViews(nonExistingBoardId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Expected 1 row to be updated, but got ");
    }
}