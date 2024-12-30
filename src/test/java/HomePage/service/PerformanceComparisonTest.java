package HomePage.service;

import HomePage.service.peformance.PerformanceTestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class PerformanceComparisonTest {
    @Autowired
    private PerformanceTestService performanceTestService;

    @Test
    public void testQueryPerformance() {
        performanceTestService.comparePerformance(466L); // 테스트할 레스토랑 ID
    }
}
