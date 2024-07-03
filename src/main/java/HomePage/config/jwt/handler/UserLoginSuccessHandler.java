package HomePage.config.jwt.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
@Component
public class UserLoginSuccessHandler implements AuthenticationSuccessHandler{
    private final OAuth2AuthorizedClientService authorizedClientService;

    public UserLoginSuccessHandler(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("onAuthenticationSuccess 실행");
        if (authentication instanceof OAuth2AuthenticationToken) {
            System.out.println("onAuthenticationSuccess 실행");
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            String registrationId = oauth2Token.getAuthorizedClientRegistrationId();
            String principalName = oauth2Token.getName();

            OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(registrationId, principalName);
            String accessToken = authorizedClient.getAccessToken().getTokenValue();

           // 응답 헤더에 액세스 토큰 추가
            //response.addHeader("Authorization", "Bearer " + accessToken);
            // 쿠키에 액세스 토큰 추가
            Cookie accessTokenCookie = new Cookie("access_token", accessToken);
            accessTokenCookie.setHttpOnly(true); // JavaScript에서 접근 불가
            accessTokenCookie.setSecure(true); // HTTPS에서만 전송
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(60 * 60); // 1시간 (필요에 따라 조정)
            response.addCookie(accessTokenCookie);

           // 원하는 경로로 리다이렉트
            UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentContextPath().path("/");
            System.out.println(uriBuilder.toUriString());
            response.sendRedirect(uriBuilder.toUriString());
        }
    }
}
