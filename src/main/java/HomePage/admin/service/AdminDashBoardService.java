package HomePage.admin.service;

import HomePage.domain.model.StatDTO;
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


}
