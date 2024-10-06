package HomePage.service;

import HomePage.controller.board.form.CommunityBoardWriteForm;
import HomePage.domain.model.entity.CommunityBoard;
import HomePage.domain.model.entity.CommunityComment;
import HomePage.domain.model.entity.Page;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@TestPropertySource(properties = {"communityBoard.page-size=10"})
class CommunityBoardServiceUnitTest {
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
    void validateCommunityForm_Success() {
        //given : 준비
        CommunityBoardWriteForm form = new CommunityBoardWriteForm();
        form.setTitle("Valid Title");
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
    void getBoardPage_Success() {
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
    void getBoardPage_InvalidPageNumber() {
        // given
        int invalidPageNumber = 0;

        // 모킹 추가 (실제로는 호출되지 않아야 함)
        when(boardRepository.count()).thenReturn(100);
        when(boardRepository.findPage(anyInt(), anyInt())).thenReturn(new ArrayList<>());

        // when & then
        assertThatThrownBy(() -> boardService.getBoardPage(invalidPageNumber))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Page number must be greater than 0");

        // 리포지토리 메서드가 호출되지 않았는지 확인
        verify(boardRepository, never()).count();
        verify(boardRepository, never()).findPage(anyInt(), anyInt());
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
    void saveBoard_Success() {
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
    void saveBoard_Failure() {
       //given: 준비
       CommunityBoard boardToSave = new CommunityBoard();
       boardToSave.setTitle("Test Title");
       boardToSave.setContent("Test Content");

       when(boardRepository.save(boardToSave)).thenThrow(new RuntimeException("Database connection failed"));
       //when & then : 실행 및 검증
       assertThatThrownBy(() -> boardService.saveBoard(boardToSave))
           .isInstanceOf(RuntimeException.class)
           .hasMessageContaining("Database connection failed");

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
    void deleteBoard_Success() {
        //given: 준비
        Long boardId = 2L;
        CommunityBoard nonDeleteBoard = new CommunityBoard();
        nonDeleteBoard.setId(boardId);
        nonDeleteBoard.setTitle("Non-delete Title");
        nonDeleteBoard.setContent("Non-delete Content");
        nonDeleteBoard.setWriter("testWriter");

        when(commentRepository.deleteByBoardId(boardId)).thenReturn(true);
        when(boardRepository.deleteById(boardId)).thenReturn(true);
        //when & then: 실행 및 검증
        assertThatCode(() -> boardService.deleteBoard(boardId))
                    .doesNotThrowAnyException();
        verify(commentRepository).deleteByBoardId(boardId);
        verify(boardRepository).deleteById(boardId);

    }
    @Test
    void deleteBoard_failure_case() {
        //given: 준비
        Long boardId = 2L;
        CommunityBoard nonDeleteBoard = new CommunityBoard();
        nonDeleteBoard.setId(boardId);
        nonDeleteBoard.setTitle("Non-delete Title");
        nonDeleteBoard.setContent("Non-delete Content");

        when(commentRepository.deleteByBoardId(boardId)).thenReturn(true);
        when(boardRepository.deleteById(boardId)).thenReturn(false);
        //when & then: 실행 및 검증
        assertThatCode(() -> boardService.deleteBoard(boardId))
                .isInstanceOf(RuntimeException.class)
                        .hasMessageContaining("Failed to delete board with board_id: " + boardId);
        verify(commentRepository).deleteByBoardId(boardId);
        verify(boardRepository).deleteById(boardId);
    }

    @Test
    void deleteBoard_failure_comment_case() {
        //given: 준비
        Long boardId = 2L;
        CommunityBoard nonDeleteBoard = new CommunityBoard();
        nonDeleteBoard.setId(boardId);
        nonDeleteBoard.setTitle("Non-delete Title");
        nonDeleteBoard.setContent("Non-delete Content");

        CommunityComment comment = new CommunityComment();
        comment.setBoard_id(boardId);
        comment.setId(1L);

        when(commentRepository.deleteByBoardId(boardId)).thenReturn(false);
        when(boardRepository.deleteById(boardId)).thenReturn(true);
        //when & then: 실행 및 검증
        assertThatCode(() -> boardService.deleteBoard(boardId))
                .isInstanceOf(RuntimeException.class)
                        .hasMessageContaining("Failed to delete comment with board_id: " + boardId);
        verify(commentRepository).deleteByBoardId(boardId);
        verify(boardRepository, never()).deleteById(boardId);
    }



    @Test
    void searchBoardsByTitle_Success() {
        //given : 준비
        CommunityBoard communityBoard = new CommunityBoard();
        communityBoard.setTitle("test");
        communityBoard.setContent("test content");
        communityBoard.setWriter("testWriter");

        // Mocking repository method
        List<CommunityBoard> mockBoardList = new ArrayList<>();
        mockBoardList.add(communityBoard);
        when(boardRepository.selectByTitle("test")).thenReturn(mockBoardList);

        //when : 실행
        List<CommunityBoard> communityBoards = boardService.searchBoardsByTitle("test");

        //then : 검증
        assertThat(communityBoards).isNotNull();  // 결과가 null이 아님을 검증
        assertThat(communityBoards.size()).isEqualTo(1);  // 결과 리스트의 크기가 예상대로 1인지 검증
        assertThat(communityBoards.get(0).getTitle()).isEqualTo("test");  // 검색된 게시글의 제목이 올바른지 검증
        assertThat(communityBoards.get(0).getContent()).isEqualTo("test content");  // 검색된 게시글의 내용이 올바른지 검증
        assertThat(communityBoards.get(0).getWriter()).isEqualTo("testWriter");  // 검색된 게시글의 작성자가 올바른지 검증

        verify(boardRepository, times(1)).selectByTitle("test");
    }
    @Test
    void searchBoardsByTitle_NoResult() {
        //boardRepository.selectByTitle() 모킹
        when(boardRepository.selectByTitle("NonexistentTitle")).thenReturn(new ArrayList<>());
        //when : 실행
        List<CommunityBoard> result = boardService.searchBoardsByTitle("NonexistentTitle");
        //then : 검증
        assertThat(result).isEmpty();
        verify(boardRepository).selectByTitle("NonexistentTitle");
    }

    @Test
    void searchBoardsByWriter_Success() {
        //given : 준비
        CommunityBoard communityBoard = new CommunityBoard();
        communityBoard.setTitle("test");
        communityBoard.setContent("test content");
        communityBoard.setWriter("testWriter");

        // Mocking repository method
        List<CommunityBoard> mockBoardList = new ArrayList<>();
        mockBoardList.add(communityBoard);
        when(boardRepository.selectByWriter("testWriter")).thenReturn(mockBoardList);

        //when : 실행
        List<CommunityBoard> communityBoards = boardService.searchBoardsByWriter("testWriter");

        //then : 검증
        assertThat(communityBoards).isNotEmpty();  // 결과가 null이 아님을 검증
        assertThat(communityBoards.size()).isEqualTo(1);  // 결과 리스트의 크기가 예상대로 1인지 검증
        assertThat(communityBoards.get(0).getTitle()).isEqualTo("test");  // 검색된 게시글의 제목이 올바른지 검증
        assertThat(communityBoards.get(0).getContent()).isEqualTo("test content");  // 검색된 게시글의 내용이 올바른지 검증
        assertThat(communityBoards.get(0).getWriter()).isEqualTo("testWriter");  // 검색된 게시글의 작성자가 올바른지 검증

        verify(boardRepository, times(1)).selectByWriter("testWriter");
    }
    @Test
    void searchBoardsByWriter_NoResult() {
        when(boardRepository.selectByWriter("NonexistentWriter")).thenReturn(new ArrayList<>());

        List<CommunityBoard> result = boardService.searchBoardsByWriter("NonexistentWriter");

        assertThat(result).isEmpty();
        verify(boardRepository).selectByWriter("NonexistentWriter");
    }
    @Test
    void getAllBoards_Success() {
        //given : 준비
        CommunityBoard communityBoard = new CommunityBoard();
        communityBoard.setTitle("test");
        communityBoard.setContent("test content");
        communityBoard.setWriter("testWriter");
        // Mocking repository method
        List<CommunityBoard> mockBoardList = new ArrayList<>();
        mockBoardList.add(communityBoard);
        when(boardRepository.selectAll()).thenReturn(mockBoardList);

        //when : 실행
        List<CommunityBoard> allBoards = boardService.getAllBoards();

        //then : 검증
        assertThat(allBoards).isNotNull();
        assertThat(allBoards.size()).isEqualTo(1);

        verify(boardRepository).selectAll();
    }

    @Test
    void getAllBoards_EmptyList() {
        when(boardRepository.selectAll()).thenReturn(new ArrayList<>());

        List<CommunityBoard> result = boardService.getAllBoards();

        assertThat(result).isEmpty();
        verify(boardRepository).selectAll();
    }

    @Test
    void getBoardByIdAndIncrementViews_Success() {
        //given : 준비
        Long boardId = 1L;
        CommunityBoard communityBoard = new CommunityBoard();
        communityBoard.setId(boardId);
        communityBoard.setTitle("test");
        communityBoard.setContent("test content");
        communityBoard.setWriter("testWriter");

        //boardRepository.selectById() 모킹, boardRepository.incrementViews() 모킹
        when(boardRepository.selectById(boardId)).thenReturn(Optional.of(communityBoard));
        when(boardRepository.incrementViews(boardId)).thenReturn(true);
        //when : 실행
        CommunityBoard result = boardService.getBoardByIdAndIncrementViews(boardId);

        // then : 검증
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(boardId);
        assertThat(result.getTitle()).isEqualTo("test");
        assertThat(result.getContent()).isEqualTo("test content");
        assertThat(result.getWriter()).isEqualTo("testWriter");

        // incrementViews 메서드가 호출되었는지 확인
        verify(boardRepository).selectById(boardId);
        verify(boardRepository).incrementViews(boardId);
    }
    @Test
    void getBoardByIdAndIncrementViews_NonExistingBoard() {
        Long nonExistingBoardId = 999L;

        when(boardRepository.selectById(nonExistingBoardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.getBoardByIdAndIncrementViews(nonExistingBoardId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Board not found with id: " + nonExistingBoardId);

        verify(boardRepository).selectById(nonExistingBoardId);
        verify(boardRepository, never()).incrementViews(nonExistingBoardId);
    }

    @Test
    void incrementViews_Success() {
        // given : 준비
        Long boardId = 1L;
        when(boardRepository.incrementViews(boardId)).thenReturn(true);

        // when : 실행
        boardService.incrementViews(boardId);

        // then : 검증
        verify(boardRepository).incrementViews(boardId);
    }

    @Test
    void incrementViews_Failure() {
        // given : 준비
        Long boardId = 1L;
        when(boardRepository.incrementViews(boardId)).thenReturn(false);

        // when & then : 실행 및 예외 검증
        assertThatThrownBy(() -> boardService.incrementViews(boardId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Expected 1 row to be updated, but got ");

        verify(boardRepository).incrementViews(boardId);
    }
}