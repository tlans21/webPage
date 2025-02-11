package HomePage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CsrfTokenRepository;

@Configuration
public class CsrfConfig {
    @Bean
    public CsrfTokenRepository customCsrfTokenRepository() {
        return new CustomCookieTokenRepository();  // sslEnabled 값 전달
    }
}
