package HomePage.service.peformance;

import HomePage.config.auth.PrincipalDetails;
import HomePage.domain.model.dto.RestaurantReviewCommentDTO;
import HomePage.service.RestaurantReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PerformanceTestService {
    @Autowired
    private RestaurantReviewService restaurantReviewService;

    public void comparePerformance(Long restaurantId, Authentication authentication) {
        int iterations = 100;
        long totalWithoutJoin = 0;
        long totalWithJoin = 0;

        for (int i = 0; i < iterations; i++) {
            // Without JOIN
            long start = System.nanoTime();
            List<RestaurantReviewCommentDTO> withoutJoin = restaurantReviewService.findByRestaurantIdWithoutJoin(restaurantId);
            totalWithoutJoin += System.nanoTime() - start;

            // With JOIN
            start = System.nanoTime();
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            Long userId = principalDetails.getUser().getId();
            List<RestaurantReviewCommentDTO> withJoin = restaurantReviewService.findByRestaurantIdWithJoin(restaurantId, userId);
            totalWithJoin += System.nanoTime() - start;
        }

        // 평균 시간 계산 (나노초를 밀리초로 변환)
        double avgWithoutJoin = totalWithoutJoin / (iterations * 1_000_000.0);
        double avgWithJoin = totalWithJoin / (iterations * 1_000_000.0);

        // 결과 출력
        System.out.printf("Average time without JOIN: %.2f ms%n", avgWithoutJoin);
        System.out.printf("Average time with JOIN: %.2f ms%n", avgWithJoin);
        System.out.printf("Performance difference: %.2f ms%n", avgWithoutJoin - avgWithJoin);
        System.out.printf("JOIN is %.2f%% %s%n",
            Math.abs((avgWithoutJoin - avgWithJoin) / avgWithoutJoin * 100),
            avgWithJoin < avgWithoutJoin ? "faster" : "slower");
    }
}
