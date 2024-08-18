package HomePage.service;

import HomePage.controller.board.form.CommunityBoardWriteForm;
import HomePage.domain.model.CommunityBoard;
import HomePage.domain.model.Page;
import HomePage.repository.JdbcTemplateCommunityBoardRepository;
import HomePage.repository.JdbcTemplateCommunityCommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.TestPropertySource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.util.*;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestPropertySource(properties = {"communityBoard.page-size=10"})
class CommunityBoardServiceTest {
    @Mock
    JdbcTemplateCommunityBoardRepository boardRepository;
    @Mock
    JdbcTemplateCommunityCommentRepository commentRepository;
    private CommunityBoardService boardService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        boardService = new CommunityBoardService(boardRepository, commentRepository);
    }

    @Test
    void validateCommunityForm() {
        //given : 준비
        CommunityBoardWriteForm form = new CommunityBoardWriteForm();
        form.setTitle("");
        form.setContent("");

        Errors errors = new BeanPropertyBindingResult(form, "communityBoardWriteForm"); // 에러 객체 생성
        if (form.getTitle() == null || form.getTitle().trim().isEmpty()) {
            errors.rejectValue("title", "NotBlank", "제목을 입력해주세요.");
        }

        if (form.getContent() == null || form.getContent().trim().isEmpty()) {
            errors.rejectValue("content", "NotBlank", "내용을 입력해주세요.");
        }


        //when : 실행
        Map<String, String> validatorResult = boardService.validateCommunityForm(errors);

        //then : 검증  "valid_%s" %s는 필드 이름
        assertThat(validatorResult).isNotEmpty();
        assertThat(validatorResult).containsKey("valid_title");
        assertThat(validatorResult.get("valid_title")).isEqualTo("제목을 입력해주세요.");
        assertThat(validatorResult).containsKey("valid_content");
        assertThat(validatorResult.get("valid_content")).isEqualTo("내용을 입력해주세요.");
    }

    @Test
    void getBoardPage() {
        //given : 준비
        int pageNumber = 1;
        int pageSize = 10; // 서비스에 정의된 pageSize와 동일함.
        int totalBoards = 100;
        int totalPages = (int) Math.ceil((double) totalBoards / pageSize);
        int offset = (pageNumber - 1) * pageSize;


        List<CommunityBoard> expectedBoards = new ArrayList<>();
        for (int i = 0; i < pageSize; i++) {
           CommunityBoard board = new CommunityBoard();
           board.setId((long) i);
           board.setTitle("Test Title " + i);
           board.setContent("Test Content " + i);
           expectedBoards.add(board);
        }
        // boardRepository.count()를 모킹
        when(boardRepository.count()).thenReturn(totalBoards);

        // boardRepository.findPage()를 모킹
        when(boardRepository.findPage(offset, pageSize)).thenReturn(expectedBoards);

        //when : 실행
        Page<CommunityBoard> result = boardService.getBoardPage(pageNumber);
        //then : 검증
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(pageSize);
        assertThat(result.getCurrentPage()).isEqualTo(pageNumber);
        assertThat(result.getTotalPages()).isEqualTo(totalPages);
        assertThat(result.getPageSize()).isEqualTo(pageSize);

        for (int i = 0; i < pageSize; i++) {
           CommunityBoard board = result.getContent().get(i);
           assertThat(board.getTitle()).isEqualTo("Test Title " + i);
           assertThat(board.getContent()).isEqualTo("Test Content " + i);
       }

       // boardRepository 메서드들이 올바른 파라미터로 호출되었는지 확인
       verify(boardRepository).count();
       verify(boardRepository).findPage(eq((pageNumber - 1) * pageSize), eq(pageSize));
    }

    @Test
    void getTopViewedBoardPage() {
        //given : 준비
        int pageNumber = 1;
        int pageSize = 10; // 서비스에 정의된 pageSize와 동일함.
        int totalBoards = 100;
        int totalPages = (int) Math.ceil((double) totalBoards / pageSize);
        int offset = (pageNumber - 1) * pageSize;


        List<CommunityBoard> expectedBoards = new ArrayList<>();
        for (int i = 0; i < pageSize; i++) {
           CommunityBoard board = new CommunityBoard();
           board.setId((long) i);
           board.setTitle("Test Title " + i);
           board.setContent("Test Content " + i);
           board.setViewCnt(i);
           expectedBoards.add(board);
        }

        //조회순으로 내림차순 정렬
        expectedBoards.sort(Comparator.comparing(CommunityBoard::getViewCnt).reversed());

        // boardRepository.count()를 모킹
        when(boardRepository.count()).thenReturn(totalBoards);

        // boardRepository.findPageOrderByTopView()를 모킹
        when(boardRepository.findPageOrderByTopView(offset, pageSize)).thenReturn(expectedBoards);

        //when : 실행
        Page<CommunityBoard> result = boardService.getTopViewedBoardPage(pageNumber);
        //then : 검증
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(pageSize);
        assertThat(result.getCurrentPage()).isEqualTo(pageNumber);
        assertThat(result.getTotalPages()).isEqualTo(totalPages);
        assertThat(result.getPageSize()).isEqualTo(pageSize);

        // 조회수 내림차순 정렬 확인
        for (int i = 0; i < pageSize - 1; i++) {
            assertThat(result.getContent().get(i).getViewCnt())
                .isGreaterThanOrEqualTo(result.getContent().get(i + 1).getViewCnt());
        }

        // boardRepository 메서드들이 올바른 파라미터로 호출되었는지 확인
        verify(boardRepository).count();
        verify(boardRepository).findPageOrderByTopView(offset, pageSize);
    }

    @Test
    void getTopCommentCntBoardPage() {
        //given : 준비
        int pageNumber = 1;
        int pageSize = 10; // 서비스에 정의된 pageSize와 동일함.
        int totalBoards = 100;
        int totalPages = (int) Math.ceil((double) totalBoards / pageSize);
        int offset = (pageNumber - 1) * pageSize;


        List<CommunityBoard> expectedBoards = new ArrayList<>();
        for (int i = 0; i < pageSize; i++) {
           CommunityBoard board = new CommunityBoard();
           board.setId((long) i);
           board.setTitle("Test Title " + i);
           board.setContent("Test Content " + i);
           board.setCommentCnt(i);
           expectedBoards.add(board);
        }

        //댓글순으로 내림차순 정렬
        expectedBoards.sort(Comparator.comparing(CommunityBoard::getCommentCnt).reversed());

        // boardRepository.count()를 모킹
        when(boardRepository.count()).thenReturn(totalBoards);

        // boardRepository.findPageOrderByTopCommentCnt()를 모킹
        when(boardRepository.findPageOrderByTopCommentCnt(offset, pageSize)).thenReturn(expectedBoards);

        //when : 실행
        Page<CommunityBoard> result = boardService.getTopCommentCntBoardPage(pageNumber);
        //then : 검증
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(pageSize);
        assertThat(result.getCurrentPage()).isEqualTo(pageNumber);
        assertThat(result.getTotalPages()).isEqualTo(totalPages);
        assertThat(result.getPageSize()).isEqualTo(pageSize);

        // 댓글 내림차순 정렬 확인
        for (int i = 0; i < pageSize - 1; i++) {
            assertThat(result.getContent().get(i).getCommentCnt())
                .isGreaterThanOrEqualTo(result.getContent().get(i + 1).getCommentCnt());
        }

        // boardRepository 메서드들이 올바른 파라미터로 호출되었는지 확인
        verify(boardRepository).count();
        verify(boardRepository).findPageOrderByTopCommentCnt(offset, pageSize);
    }

    @Test
    void getBoardById_ExistingBoard() {
        // given : 준비
        Long boardId = 1L;
        CommunityBoard expectedBoard = new CommunityBoard();
        expectedBoard.setId(boardId);
        expectedBoard.setTitle("Test Title");
        expectedBoard.setContent("Test Content");

        when(boardRepository.selectById(boardId)).thenReturn(Optional.of(expectedBoard));

        // when : 실행
        CommunityBoard result = boardService.getBoardById(boardId);

        // then : 검증
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(boardId);
        assertThat(result.getTitle()).isEqualTo("Test Title");
        assertThat(result.getContent()).isEqualTo("Test Content");

        verify(boardRepository).selectById(boardId);
    }
    @Test
    void getBoardById_NonExistingBoard() {
        // given : 준비
        Long nonExistingBoardId = 999L;

        when(boardRepository.selectById(nonExistingBoardId)).thenReturn(Optional.empty());

        // when & then : 실행 및 검증
        assertThatThrownBy(() -> boardService.getBoardById(nonExistingBoardId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Board not found with id: " + nonExistingBoardId);

        verify(boardRepository).selectById(nonExistingBoardId);
    }

    @Test
    void saveBoard() {
        // given : 준비
        Long boardId = 1L;
        CommunityBoard boardToSave = new CommunityBoard();
        boardToSave.setId(boardId);
        boardToSave.setTitle("Test Title");
        boardToSave.setContent("Test Content");

        when(boardRepository.save(boardToSave)).thenReturn(boardToSave);

        // when : 실행
        boardService.saveBoard(boardToSave);

        // then : 검증
        verify(boardRepository).save(boardToSave);
    }

    @Test
    void updateBoard_Success() {
        // given : 준비
        Long boardId = 1L;
        CommunityBoard existingBoard = new CommunityBoard();
        existingBoard.setId(boardId);
        existingBoard.setTitle("Original Title");
        existingBoard.setContent("Original Content");

        CommunityBoard updatedBoard = new CommunityBoard();
        updatedBoard.setId(boardId);
        updatedBoard.setTitle("Updated Title");
        updatedBoard.setContent("Updated Content");

        when(boardRepository.update(updatedBoard)).thenReturn(true);

        // when & then : 실행 및 검증
        assertThatCode(() -> boardService.updateBoard(updatedBoard))
            .doesNotThrowAnyException();

        verify(boardRepository).update(updatedBoard);
        // 업데이트된 게시글 내용 확인
        assertThat(updatedBoard.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedBoard.getContent()).isEqualTo("Updated Content");
    }
    @Test
    void updateBoard_Failure() {
        //given : 준비
        Long boardId = 2L;
        CommunityBoard nonExistentBoard = new CommunityBoard();
        nonExistentBoard.setId(boardId);
        nonExistentBoard.setTitle("Non-existent Title");
        nonExistentBoard.setContent("Non-existent Content");

        // when & then : 실행 및 검증
        assertThatThrownBy(() -> boardService.updateBoard(nonExistentBoard))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to update board with id: " + boardId);

        verify(boardRepository).update(nonExistentBoard);

    }

    @Test
    void deleteBoard() {
    }

    @Test
    void searchBoardsByTitle() {
    }

    @Test
    void searchBoardsByWriter() {
    }

    @Test
    void getAllBoards() {
    }

    @Test
    void getBoardByIdAndIncrementViews() {
    }

    @Test
    void incrementViews() {
    }
}