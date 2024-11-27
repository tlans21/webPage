package HomePage.batch.scheduler;

import HomePage.batch.job.NicknameSyncJob;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchScheduler {
    private final NicknameSyncJob nicknameSyncJob;

    @Scheduled(initialDelay = 5000, fixedRate = 1000000)
    public void runNicknameSync() {
        nicknameSyncJob.syncNicknames();
    }
}
