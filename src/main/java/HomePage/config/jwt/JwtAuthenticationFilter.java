package HomePage.config.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // Todo: 1. username, password를 받아서
        // Todo: 2. 정상적인 유저인지 필터로 확인한다.
        // Todo: 3. PrinciaplDetailsService의 loadUsername() 메서드 실행
        // Todo: 4. PrinciaplDetails를 세션의 담는다. (권한 관리를 위해서)
        // Todo: 5. JWT토큰을 만들어 응답
        System.out.println("JwtAuthentication : 로그인 시도중");
        return super.attemptAuthentication(request, response);
    }
}
