package HomePage.repository;

import HomePage.domain.model.CommunityBoard;
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

@SpringBootTest
@Transactional
class JdbcTemplateCommunityBoardRepositoryIntegrationTest {
    @Autowired
    BoardRepository<CommunityBoard> boardRepository;
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
            // 먼저 댓글 삭제
           jdbcTemplate.execute("DELETE FROM test.community_comment WHERE board_id > 1");
           // 그 다음 게시글 삭제
           jdbcTemplate.execute("DELETE FROM test.community_board WHERE board_id > 1");
        } finally {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
            jdbcTemplate.execute("SET SQL_SAFE_UPDATES = 1");
        }
    }
    @Test
    void save() {
        //given : 준비
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRoles("ROLE_USER");
        userRepository.save(user);

        CommunityBoard board = new CommunityBoard();
        board.setTitle("test");
        board.setContent("testContent");
        board.setWriter("testUser");


        //when : 실행
        CommunityBoard savedBoard = boardRepository.save(board);
        //then : 검증
        assertThat(savedBoard.getId()).isNotNull();
        assertThat(savedBoard.getRegisterDate()).isNotNull();
        assertThat(savedBoard.getTitle()).isEqualTo("test");
        assertThat(savedBoard.getContent()).isEqualTo("testContent");
        assertThat(savedBoard.getWriter()).isEqualTo("testUser");

    }

    @Test
    void findPage() {
        //given : 준비
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRoles("ROLE_USER");
        userRepository.save(user);


        // 테스트 데이터 생성
        for (int i = 1; i <= 20; i++) {
            CommunityBoard board = new CommunityBoard();
            board.setTitle("Test Title " + i);
            board.setContent("Test Content " + i);
            board.setWriter(user.getUsername());
            boardRepository.save(board);
        }

        //when : 실행
        int offset = 5;
        int limit = 10;
        List<CommunityBoard> result = boardRepository.findPage(offset, limit);

        //then : 검증
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(limit);

        // 결과가 최신순으로 정렬되어 있는지 확인
        for (int i = 0; i < result.size() - 1; i++) {
            assertThat(result.get(i).getRegisterDate())
                .isAfterOrEqualTo(result.get(i + 1).getRegisterDate());
        }

        // offset이 적용되었는지 확인
        CommunityBoard firstBoard = result.get(0);
        assertThat(firstBoard.getTitle()).isEqualTo("Test Title " + (20 - offset));

        // limit가 적용되었는지 확인
        CommunityBoard lastBoard = result.get(result.size() - 1);
        assertThat(lastBoard.getTitle()).isEqualTo("Test Title " + (20 - (offset + limit - 1)));
    }

    @Test
    void findPageOrderByTopView() {
        //given : 준비
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRoles("ROLE_USER");
        userRepository.save(user);


        // 테스트 데이터 생성
        for (int i = 1; i <= 20; i++) {
            CommunityBoard board = new CommunityBoard();
            board.setTitle("Test Title " + i);
            board.setContent("Test Content " + i);
            board.setWriter(user.getUsername());
            board.setViewCnt(i);

            boardRepository.save(board);
        }
        //when : 실행
        int offset = 5;
        int limit = 10;
        List<CommunityBoard> result = boardRepository.findPageOrderByTopView(offset, limit);

        //then : 검증
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(limit);

        // 결과가 죄회순으로 정렬되어 있는지 확인
        for (int i = 0; i < result.size() - 1; i++) {
            assertThat(result.get(i).getViewCnt())
                .isGreaterThanOrEqualTo(result.get(i + 1).getViewCnt()); // 내림차순 확인 왼쪽(result.get(i).getViewCnt())값이 오른쪽(result.get(i + 1).getViewCnt()) 값보다 크거나 같은지 비교
        }

        // offset이 적용되었는지 확인
        CommunityBoard firstBoard = result.get(0);
        assertThat(firstBoard.getViewCnt()).isEqualTo(20 - offset);

        // limit가 적용되었는지 확인
        CommunityBoard lastBoard = result.get(result.size() - 1);
        assertThat(lastBoard.getViewCnt()).isEqualTo(20 - (offset + limit - 1));
    }

    @Test
    void findPageOrderByTopCommentCnt() {
        //given : 준비
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRoles("ROLE_USER");
        userRepository.save(user);

        // 테스트 데이터 생성
        for (int i = 1; i <= 20; i++) {
            CommunityBoard board = new CommunityBoard();
            board.setTitle("Test Title " + i);
            board.setContent("Test Content " + i);
            board.setWriter(user.getUsername());
            board.setCommentCnt(i);  // 댓글 수를 i로 설정
            boardRepository.save(board);
        }

        //when : 실행
        int offset = 5;
        int limit = 10;
        List<CommunityBoard> result = boardRepository.findPageOrderByTopCommentCnt(offset, limit);

        //then : 검증
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(limit);

        // 결과가 댓글 수 순으로 정렬되어 있는지 확인
        for (int i = 0; i < result.size() - 1; i++) {
            assertThat(result.get(i).getCommentCnt())
                .isGreaterThanOrEqualTo(result.get(i + 1).getCommentCnt());
        }

        // offset이 적용되었는지 확인
        CommunityBoard firstBoard = result.get(0);
        assertThat(firstBoard.getCommentCnt()).isEqualTo(20 - offset);

        // limit가 적용되었는지 확인
        CommunityBoard lastBoard = result.get(result.size() - 1);
        assertThat(lastBoard.getCommentCnt()).isEqualTo(20 - (offset + limit - 1));
    }

    @Test
    void count() {
        //given : 준비
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRoles("ROLE_USER");
        userRepository.save(user);

        for(int i = 1; i <= 20; i++){
            CommunityBoard board = new CommunityBoard();
            board.setTitle("Test Title " + i);
            board.setContent("Test Content " + i);
            board.setWriter(user.getUsername());
            board.setCommentCnt(i);  // 댓글 수를 i로 설정
            boardRepository.save(board);
        }
        //when : 실행
        int count = boardRepository.count();
        //then : 검증
        assertThat(count).isEqualTo(20);

    }

    @Test
    void update() throws InterruptedException {
        //given : 준비
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRoles("ROLE_USER");
        userRepository.save(user);

        CommunityBoard board = new CommunityBoard();
        board.setTitle("Title Before Modification");
        board.setContent("Content Before Modification");
        board.setWriter(user.getUsername());
        CommunityBoard savedBoard = boardRepository.save(board);

        // 수정할 내용 설정
        savedBoard.setTitle("Modified Title");
        savedBoard.setContent("Modified Content");


        //when : 실행
        Thread.sleep(1000);
        boolean isUpdate = boardRepository.update(savedBoard);
        //then : 검증
        assertThat(isUpdate).isTrue();
        // 수정된 게시글 다시 조회
        Optional<CommunityBoard> updatedBoardOptional = boardRepository.selectById(savedBoard.getId());
        assertThat(updatedBoardOptional).isPresent();

        CommunityBoard updatedBoard = updatedBoardOptional.get();
        assertThat(updatedBoard.getTitle()).isEqualTo("Modified Title");
        assertThat(updatedBoard.getContent()).isEqualTo("Modified Content");
        assertThat(updatedBoard.getWriter()).isEqualTo(user.getUsername());
        assertThat(updatedBoard.getUpdateDate()).isNotNull();
        assertThat(updatedBoard.getUpdateDate()).isAfter(updatedBoard.getRegisterDate());
    }

    @Test
    void deleteById() {
        //given : 준비
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRoles("ROLE_USER");
        userRepository.save(user);

        CommunityBoard board = new CommunityBoard();
        board.setTitle("Test Title");
        board.setContent("Test Content");
        board.setWriter(user.getUsername());
        CommunityBoard savedBoard = boardRepository.save(board);
        //when : 실행
        boolean isDelete = boardRepository.deleteById(savedBoard.getId());
        //then : 검증
        assertThat(isDelete).isTrue();

        Optional<CommunityBoard> optionalFoundBoard = boardRepository.selectById(savedBoard.getId());
        assertThat(optionalFoundBoard).isNotPresent();

    }

    @Test
    void selectById() {
        //given : 준비
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRoles("ROLE_USER");
        userRepository.save(user);

        CommunityBoard board = new CommunityBoard();
        board.setTitle("Test Title");
        board.setContent("Test Content");
        board.setWriter(user.getUsername());
        CommunityBoard savedBoard = boardRepository.save(board);

        //when : 실행
        Optional<CommunityBoard> optionalFoundBoard = boardRepository.selectById(savedBoard.getId());
        //then : 검증
        assertThat(optionalFoundBoard).isPresent();
        CommunityBoard foundBoard = optionalFoundBoard.get();
        assertThat(foundBoard.getTitle()).isEqualTo(board.getTitle());
        assertThat(foundBoard.getContent()).isEqualTo(board.getContent());
        assertThat(foundBoard.getWriter()).isEqualTo(board.getWriter());

    }

    @Test
    void selectByTitle() {
        //given : 준비
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRoles("ROLE_USER");
        userRepository.save(user);

        CommunityBoard board1 = new CommunityBoard();
        board1.setTitle("Test Title 1");
        board1.setContent("Test Content 1");
        board1.setWriter(user.getUsername());
        boardRepository.save(board1);

        CommunityBoard board2 = new CommunityBoard();
        board2.setTitle("Test Title 2");
        board2.setContent("Test Content 2");
        board2.setWriter(user.getUsername());
        boardRepository.save(board2);

        CommunityBoard board3 = new CommunityBoard();
        board3.setTitle("Different Title");
        board3.setContent("Test Content 3");
        board3.setWriter(user.getUsername());
        boardRepository.save(board3);

        //when : 실행
        List<CommunityBoard> foundBoards = boardRepository.selectByTitle("Test");

        //then : 검증
        assertThat(foundBoards).hasSize(2);
        assertThat(foundBoards).extracting("title")
            .containsExactlyInAnyOrder("Test Title 1", "Test Title 2");
    }

    @Test
    void selectByWriter() {
        //given : 준비
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password");
        user1.setRoles("ROLE_USER");
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password");
        user2.setRoles("ROLE_USER");
        userRepository.save(user2);

        CommunityBoard board1 = new CommunityBoard();
        board1.setTitle("Title 1");
        board1.setContent("Content 1");
        board1.setWriter(user1.getUsername());
        boardRepository.save(board1);

        CommunityBoard board2 = new CommunityBoard();
        board2.setTitle("Title 2");
        board2.setContent("Content 2");
        board2.setWriter(user1.getUsername());
        boardRepository.save(board2);

        CommunityBoard board3 = new CommunityBoard();
        board3.setTitle("Title 3");
        board3.setContent("Content 3");
        board3.setWriter(user2.getUsername());
        boardRepository.save(board3);

        //when : 실행
        List<CommunityBoard> foundBoards = boardRepository.selectByWriter(user1.getUsername());

        //then : 검증
        assertThat(foundBoards).hasSize(2);
        assertThat(foundBoards).extracting("writer")
            .containsOnly(user1.getUsername());
    }

    @Test
    void selectAll() {
        //given : 준비
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRoles("ROLE_USER");
        userRepository.save(user);

        int boardCount = 5;
        for (int i = 0; i < boardCount; i++) {
            CommunityBoard board = new CommunityBoard();
            board.setTitle("Title " + i);
            board.setContent("Content " + i);
            board.setWriter(user.getUsername());
            boardRepository.save(board);
        }

        //when : 실행
        List<CommunityBoard> allBoards = boardRepository.selectAll();

        //then : 검증
        assertThat(allBoards).hasSize(boardCount);
    }

    @Test
    void incrementViews() {
        //given : 준비
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRoles("ROLE_USER");
        userRepository.save(user);

        CommunityBoard board = new CommunityBoard();
        board.setTitle("Test Title");
        board.setContent("Test Content");
        board.setWriter(user.getUsername());
        CommunityBoard savedBoard = boardRepository.save(board);
        int initialViews = savedBoard.getViewCnt();

        //when : 실행
        boolean result = boardRepository.incrementViews(savedBoard.getId());

        //then : 검증
        assertThat(result).isTrue();
        Optional<CommunityBoard> updatedBoardOptional = boardRepository.selectById(savedBoard.getId());
        assertThat(updatedBoardOptional).isPresent();
        CommunityBoard updatedBoard = updatedBoardOptional.get();
        assertThat(updatedBoard.getViewCnt()).isEqualTo(initialViews + 1);
    }

    @Test
    void updateCommentCnt() {
        //given : 준비
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRoles("ROLE_USER");
        userRepository.save(user);

        CommunityBoard board = new CommunityBoard();
        board.setTitle("Test Title");
        board.setContent("Test Content");
        board.setWriter(user.getUsername());
        CommunityBoard savedBoard = boardRepository.save(board);
        int initialCommentCnt = savedBoard.getCommentCnt();
        int increment = 2;

        //when : 실행
        boolean result = boardRepository.updateCommentCnt(savedBoard.getId(), increment);

        //then : 검증
        assertThat(result).isTrue();
        Optional<CommunityBoard> updatedBoardOptional = boardRepository.selectById(savedBoard.getId());
        assertThat(updatedBoardOptional).isPresent();
        CommunityBoard updatedBoard = updatedBoardOptional.get();
        assertThat(updatedBoard.getCommentCnt()).isEqualTo(initialCommentCnt + increment);
    }
}