package HomePage.config;

import HomePage.config.jwt.handler.UserLoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HandlerConfig {
    @Bean
    public UserLoginSuccessHandler userLoginSuccessHandler() {
            return new UserLoginSuccessHandler();
        }
}
