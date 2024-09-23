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
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    TokenProvider tokenProvider;
    @Autowired
    UserService userService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("oAuth2 로그인 성공 핸들러 작동");
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        String username = principal.getUsername();
        String accessToken = tokenProvider.createToken(principal);
        Cookie cookie = new Cookie("access_token", accessToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        System.out.println("oAuth2 최신 로그인");
        userService.updateLastLoginDate(username); // 최신 로그인 날짜 갱신
        response.sendRedirect("/");
    }
}
