package HomePage.admin.service;

import HomePage.domain.model.dto.ChartDataDTO;
import HomePage.domain.model.dto.StatDTO;
import HomePage.repository.JdbcTemplateCommunityBoardRepository;
import HomePage.repository.JdbcTemplateCommunityCommentRepository;
import HomePage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminDashBoardService {
    private final UserRepository userRepository;
    private final JdbcTemplateCommunityBoardRepository boardRepository;
    private final JdbcTemplateCommunityCommentRepository commentRepository;
    @Autowired
    public AdminDashBoardService(UserRepository userRepository, JdbcTemplateCommunityBoardRepository boardRepository, JdbcTemplateCommunityCommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
    }

    public List<StatDTO> getUserStats() {
        long totalUsers = userRepository.count();
        long newUsers = userRepository.countByCreatedAtAfter(LocalDateTime.now().minusMonths(1));
        long activeUsers = userRepository.countByLastLoginAfter(LocalDateTime.now().minusMonths(1));
        long deletedUsers = userRepository.countByDeletedAtAfter(LocalDateTime.now().minusMonths(1));
        System.out.println("newUsers : " + newUsers);
        System.out.println("1달전 : " + LocalDateTime.now().minusMonths(1));
        // 이전 달의 데이터를 가져와 변화율 계산
        long prevTotalUsers = userRepository.countByCreatedAtBefore(LocalDateTime.now().minusMonths(1));
        double totalUsersChange = calculatePercentageChange(prevTotalUsers, totalUsers);

        List<StatDTO> stats = new ArrayList<>();
        stats.add(new StatDTO("총 회원 수", totalUsers, totalUsersChange, "fas fa-users"));
        stats.add(new StatDTO("신규 가입", newUsers, 0, "fas fa-user-plus")); // 변화율 계산 생략
        stats.add(new StatDTO("활성 회원", activeUsers, 0, "fas fa-user-check")); // 변화율 계산 생략
        stats.add(new StatDTO("탈퇴 회원", deletedUsers, 0, "fas fa-user-minus")); // 변화율 계산 생략

        return stats;
    }
    public List<StatDTO> getPostStats() {
        long totalPosts = boardRepository.count();
        long newPosts = boardRepository.countByCreatedAtAfter(LocalDateTime.now().minusMonths(1));
        long totalComments = commentRepository.count();
        long newComments = commentRepository.countByCreatedAtAfter(LocalDateTime.now().minusMonths(1));

        // 이전 달의 데이터를 가져와 변화율 계산
        long prevTotalPosts = boardRepository.countByCreatedAtBefore(LocalDateTime.now().minusMonths(1));
        double totalPostsChange = calculatePercentageChange(prevTotalPosts, totalPosts);

        List<StatDTO> stats = new ArrayList<>();
        stats.add(new StatDTO("총 게시글 수", totalPosts, totalPostsChange, "fas fa-file-alt"));
        stats.add(new StatDTO("새 게시글", newPosts, 0, "fas fa-file-medical")); // 변화율 계산 생략
        stats.add(new StatDTO("총 댓글 수", totalComments, 0, "fas fa-comments")); // 변화율 계산 생략
        stats.add(new StatDTO("새 댓글", newComments, 0, "fas fa-comment-medical")); // 변화율 계산 생략

        return stats;
    }

    public List<ChartDataDTO> getUserChartData(){
        // 최근 6개월 데이터 조회
        List<ChartDataDTO> chartData = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 5; i >= 0; i--) {
            LocalDateTime start = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime end = start.plusMonths(1);
            long newUsers = userRepository.countByCreatedAtBetween(start, end);
            long deletedUsers = userRepository.countByDeletedAtBetween(start, end);
            chartData.add(new ChartDataDTO(start.getMonth().toString(), (int)newUsers, (int)deletedUsers));
        }
        return chartData;
    }

    public List<ChartDataDTO> getPostChartData(){
        // 최근 6개월 데이터 조회
        List<ChartDataDTO> chartData = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 5; i >= 0; i--) {
            LocalDateTime start = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime end = start.plusMonths(1);
            long posts = boardRepository.countByCreatedAtBetween(start, end);
            long comments = commentRepository.countByCreatedAtBetween(start, end);
            chartData.add(new ChartDataDTO(start.getMonth().toString(), (int)posts, (int)comments));
        }
        return chartData;
    }

    private double calculatePercentageChange(long oldValue, long newValue) {
        if (oldValue == 0) {
            return newValue > 0 ? 100.0 : 0.0;
        }
        return ((newValue - oldValue) / (double) oldValue) * 100.0;
    }
}
