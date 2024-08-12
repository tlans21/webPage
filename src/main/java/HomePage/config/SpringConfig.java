package HomePage.config;

import HomePage.repository.*;
import HomePage.service.CommunityBoardService;
import HomePage.service.CommunityCommentService;
import HomePage.service.ReviewBoardService;
import HomePage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SpringConfig {


    private final DataSource dataSource;
    @Autowired
    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public UserService userService(){
        return new UserService(userRepository());
    }

    @Bean
    public UserRepository userRepository(){
        return new JdbcTemplateUserRepository(dataSource);
    }


    @Bean
    public CommunityBoardService communityBoardService(){
        return new CommunityBoardService(communityBoardRepository(), communityCommentRepository());
    }

    @Bean
    public JdbcTemplateCommunityBoardRepository communityBoardRepository(){
        return new JdbcTemplateCommunityBoardRepository(dataSource);
    }

    @Bean
    public JdbcTemplateReviewBoardRepository reviewBoardBoardRepository(){
        return new JdbcTemplateReviewBoardRepository(dataSource);
    }

    @Bean
    public ReviewBoardService reviewBoardService(){
        return new ReviewBoardService(reviewBoardBoardRepository());
    }

    @Bean
    public JdbcTemplateCommunityCommentRepository communityCommentRepository(){
        return new JdbcTemplateCommunityCommentRepository(dataSource);
    }

    @Bean
    public CommunityCommentService communityCommentService(){
        return new CommunityCommentService(communityCommentRepository(), communityBoardRepository());
    }

}
