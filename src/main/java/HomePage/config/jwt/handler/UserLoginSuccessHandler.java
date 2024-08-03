package HomePage.config.jwt.handler;

import HomePage.config.auth.PrincipalDetails;
import HomePage.config.jwt.provider.TokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class UserLoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("일반 로그인 성공 핸들러 작동");
        String accessToken = tokenProvider.createToken((PrincipalDetails) authentication.getPrincipal());
        Cookie cookie = new Cookie("access_token", accessToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        response.sendRedirect("/");
    }
}
