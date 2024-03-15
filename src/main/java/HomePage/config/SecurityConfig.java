package HomePage.config;


import HomePage.config.jwt.JwtAuthenticationFilter;
import HomePage.config.jwt.JwtAuthorizationFilter;
import HomePage.config.jwt.handler.UserLoginSuccessHandler;
import HomePage.config.oauth.PrincipalOauth2UserService;
import HomePage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CorsConfig corsConfig;

    @Autowired
    private UserLoginSuccessHandler userLoginSuccessHandler;

    //AuthenticationConfiguration을 통해서 AuthenticationManager을 가져올 수 있다.

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
         return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        AuthenticationManager authenticationManager = authenticationManager(http.getSharedObject(AuthenticationConfiguration.class));
        http
//                .addFilterBefore(new Filter3(), SecurityContextHolderFilter.class)
                .csrf((csrfConfig) ->
                        csrfConfig.disable()
                )
                .sessionManagement((sessionManagementConfig) -> sessionManagementConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .formLogin((formLogin) ->formLogin.disable())
                .httpBasic((httpBasicConfig) -> httpBasicConfig.disable()
                )
                .oauth2Login((oauth2Login) ->
                                           oauth2Login
                                                   .loginPage("/loginForm")
                                                   .defaultSuccessUrl("/")
                                                   .failureUrl("/loginForm")
                                                   .userInfoEndpoint((userInfoEndpoint) ->
                                                           userInfoEndpoint
                                                                   .userService(principalOauth2UserService)
                                                   )
                )
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers("/api/v1/user/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                                .requestMatchers("/api/v1/manager/**").hasAnyRole("MANAGER", "ADMIN")
                                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                                .anyRequest().permitAll()
                );
            http
                .addFilter(corsConfig.corsFilter())
                .addFilterAt(jwtAuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthorizationFilter(authenticationManager), JwtAuthenticationFilter.class);

            return http.build();
    }


    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authenticationManager){
        try {
            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, userRepository, userLoginSuccessHandler);
            jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(userLoginSuccessHandler);
            return jwtAuthenticationFilter;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create JwtAuthenticationFilter", e);
        }
    }
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(AuthenticationManager authenticationManager){
        try{
            JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(authenticationManager, userRepository);

            return jwtAuthorizationFilter;
        } catch (Exception e){
            throw new RuntimeException("Failed to create JwtAuthenticationFilter", e);
        }
    }
}
