package HomePage.config;

import HomePage.repository.JdbcTemplateCommunityBoardRepository;
import HomePage.repository.JdbcTemplateCommunityCommentRepository;
import HomePage.repository.JdbcTemplateUserRepository;
import HomePage.repository.UserRepository;
import HomePage.service.user.UserService;
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
    public JdbcTemplateCommunityBoardRepository communityBoardRepository(){
        return new JdbcTemplateCommunityBoardRepository(dataSource);
    }

    @Bean
    public JdbcTemplateCommunityCommentRepository communityCommentRepository(){
        return new JdbcTemplateCommunityCommentRepository(dataSource);
    }
}
