package HomePage.config;

import HomePage.config.jwt.handler.UserLoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

@Configuration
public class HandlerConfig {
    @Bean
    public UserLoginSuccessHandler userLoginSuccessHandler(OAuth2AuthorizedClientService authorizedClientService) {
        return new UserLoginSuccessHandler(authorizedClientService);
    }
}
