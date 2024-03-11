package HomePage.config;


import HomePage.config.jwt.JwtAuthenticationFilter;
import HomePage.config.jwt.JwtAuthorizationFilter;
import HomePage.config.oauth.PrincipalOauth2UserService;
import HomePage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) //secured 어노테이션 활성화, preAuthorization 활성화
public class SecurityConfig{
    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CorsFilter corsFilter;


    //AuthenticationConfiguration을 통해서 AuthenticationManager을 가져올 수 있다.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Session 방식 로그인
//       http
//                .csrf((csrfConfig) ->
//                        csrfConfig.disable()
//                )
//                .authorizeHttpRequests((authorizeRequests) ->
//                        authorizeRequests
//                                .requestMatchers("/user/**").authenticated()
//                                .requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")
//                                .requestMatchers("/admin/**").hasRole("ADMIN")
//                                .anyRequest().permitAll()
//                )
//                .formLogin((formLogin) ->
//                        formLogin
//                                .loginPage("/loginForm")
//                                .loginProcessingUrl("/login")
//                                .defaultSuccessUrl("/index")
//                )
//                .oauth2Login((oauth2Login) ->
//                            oauth2Login
//                                    .loginPage("/loginForm")
//                                    .defaultSuccessUrl("/")
//                                    .failureUrl("/loginForm")
//                                    .userInfoEndpoint((userInfoEndpoint) ->
//                                            userInfoEndpoint
//                                                    .userService(principalOauth2UserService)
//                                    )
//                );
//
//        return http.build();
        // JWT 방식

        AuthenticationConfiguration authenticationConfiguration = http.getSharedObject(AuthenticationConfiguration.class);

        http
//                .addFilterBefore(new Filter3(), SecurityContextHolderFilter.class)
                .csrf((csrfConfig) ->
                        csrfConfig.disable()
                )
                .sessionManagement((sessionManagementConfig) -> sessionManagementConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilter(corsFilter)
                .formLogin((formLogin) -> formLogin.disable()
                )
                .httpBasic((httpBasicConfig) -> httpBasicConfig.disable()
                )
                .addFilter(new JwtAuthenticationFilter(authenticationManager(authenticationConfiguration)))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(authenticationConfiguration), userRepository))
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers("/api/v1/user/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                                .requestMatchers("/api/v1/manager/**").hasAnyRole("MANAGER", "ADMIN")
                                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                                .anyRequest().permitAll()
                );
        return http.build();
    }

}
