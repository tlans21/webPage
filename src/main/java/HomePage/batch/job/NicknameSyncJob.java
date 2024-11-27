package HomePage.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NicknameSyncJob {
    private final JdbcTemplate jdbcTemplate;

    public void syncNicknames(){
        String updateQuery = """
                UPDATE security.communityboard b
                INNER JOIN security.user u 
                ON b.writer = u.username
                SET b.nickname = u.nickname
                WHERE b.nickname != u.nickname
                OR b.nickname IS NULL
                """;
        int updatedCount = jdbcTemplate.update(updateQuery);
        System.out.println(updatedCount);
    }

}
