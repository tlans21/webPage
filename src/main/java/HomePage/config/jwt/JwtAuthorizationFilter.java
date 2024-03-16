package HomePage.config.jwt;

import HomePage.config.auth.PrincipalDetails;
import HomePage.domain.model.User;
import HomePage.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("인증이나 권한이 필요한 주소 요청이 됨");
        String currentUrl = request.getRequestURL().toString();
        System.out.println("Current URL: " + currentUrl);

        String jwtHeader = request.getHeader("Authorization");
        System.out.println("jwtHeader : " + jwtHeader);

        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")){
            chain.doFilter(request, response);
            return;
        }

        String jwtToken = jwtHeader.replace("Bearer", "").trim();
        System.out.println(jwtToken);
        String username =
                JWT.require(Algorithm.HMAC512("COS")).build().verify(jwtToken).getClaim("username").asString();

        if(username != null){
            User userEntity = userRepository.findByUsername(username).get();

            PrincipalDetails principalDetails = new PrincipalDetails(userEntity);

            //jwt 토큰 서명을 통해서 서명이 정상이면 Authentication 객체를 생성함.

            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

            // 세션에 authentication 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);


        }

        chain.doFilter(request, response);
    }
}
