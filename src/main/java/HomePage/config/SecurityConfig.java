package HomePage.config;


import HomePage.config.oauth.PrincipalOauth2UserService;
import org.springframework.web.filter.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) //secured 어노테이션 활성화, preAuthorization 활성화
public class SecurityConfig{
    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    @Autowired
    private CorsFilter corsFilter;



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
        http
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
