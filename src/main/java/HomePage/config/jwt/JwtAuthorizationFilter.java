package HomePage.config.jwt;

import HomePage.config.auth.PrincipalDetails;
import HomePage.config.jwt.provider.TokenProvider;
import HomePage.domain.model.entity.User;
import HomePage.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

// 스프링 시큐리티에는 filter가 존재하는데, BasicAuthorizationFilter라는 것이 존재함.
// 권한이나 인증이 필요한 특정한 주소를 요청했을 때, 위 필터를 무조건 거치게 되어 있음.
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    UserRepository userRepository;
    @Autowired
    TokenProvider tokenProvider;
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String accessToken = tokenProvider.getAccessTokenFromRequest(request);

        if (accessToken == null){  // accessToken이 없으면 종료하고 다음 체인
            chain.doFilter(request, response);
            return;
        }

        boolean isValid = tokenProvider.validateToken(accessToken);

        if (!isValid) { // 검증이 안되었으면 종료하고 다음 체인
            boolean isExpired = tokenProvider.isTokenExpired(accessToken);
            if (isExpired){
                System.out.println("액세스토큰이 만료되었습니다.");

            }
            chain.doFilter(request, response);

        }

        String username = tokenProvider.getUsernameFromToken(accessToken);

        if(username != null){
            User userEntity = userRepository.findByUsername(username).get();

            PrincipalDetails principalDetails = new PrincipalDetails(userEntity);

            //jwt 토큰 서명을 통해서 서명이 정상이면 Authentication 객체를 생성함.

            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

            // 세션에 authentication 저장이 되어야지만 접근 제한이 있는 리소스에 접근이 가능함.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

}
