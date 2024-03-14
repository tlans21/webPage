package HomePage.config.jwt;

import HomePage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class CustomFilterConfigurer extends AbstractHttpConfigurer {
    @Autowired
    private UserRepository userRepository;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        AuthenticationConfiguration authenticationConfiguration = http.getSharedObject(AuthenticationConfiguration.class);
        AuthenticationManager authenticationManager = authenticationManager(authenticationConfiguration);

       JwtAuthenticationFilter jwtAuthenticationFilter =
               new JwtAuthenticationFilter(authenticationManager, userRepository);
       jwtAuthenticationFilter.setFilterProcessesUrl("/auth/login");


//       JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenizer, authorityUtils);

       // Spring Security Filter Chain에 추가
       http.addFilter(jwtAuthenticationFilter);
    }
}
