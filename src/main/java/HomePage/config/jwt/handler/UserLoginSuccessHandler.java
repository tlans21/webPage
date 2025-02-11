package HomePage.config.jwt.handler;

import HomePage.config.auth.PrincipalDetails;
import HomePage.config.jwt.provider.TokenProvider;
import HomePage.service.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class UserLoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserService userService;

    // CSRF 관련 추가


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        System.out.println("일반 로그인 성공 핸들러 작동");
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        String username = principal.getUsername();

        // JWT 토큰 생성
        String accessToken = tokenProvider.createToken(principal);

        // 응답 헤더 확인
        Collection<String> headers = response.getHeaders("Set-Cookie");
        System.out.println("Response Cookies before JWT: " + headers);

        // JWT 토큰을 마지막에 설정
        Cookie accessTokenCookie = new Cookie("access_token", accessToken);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(false);

//        accessTokenCookie.setAttribute("SameSite", "Lux");  // SameSite 설정 추가
        accessTokenCookie.setSecure(true);  // SameSite=None을 사용할 때는 Secure도 필요
        response.addCookie(accessTokenCookie);

        userService.updateLastLoginDate(username);
        response.sendRedirect("/");
    }
}
