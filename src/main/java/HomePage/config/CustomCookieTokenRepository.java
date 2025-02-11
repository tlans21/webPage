package HomePage.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import java.time.LocalDateTime;


public class CustomCookieTokenRepository implements CsrfTokenRepository {
    private final CookieCsrfTokenRepository delegate;
    private boolean sslEnabled = false;  // 또는 주입받을 수 있음

    public CustomCookieTokenRepository() {
        this.delegate = CookieCsrfTokenRepository.withHttpOnlyFalse();

        delegate.setCookieCustomizer(cookie -> {
            if(sslEnabled){
                cookie.path("/")
                        .secure(true)
                        .httpOnly(false)
                        .maxAge(3600)
                        .sameSite("None")
                        .domain("yummuity.com");
            } else {
                cookie.path("/")
                      .secure(true)
                      .httpOnly(false)
                      .maxAge(3600)
                      .sameSite("None")
                      .domain("localhost");
            }
        });
    }

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        CsrfToken token = delegate.generateToken(request);
        System.out.println("Root path CSRF Token generated at " + LocalDateTime.now() + ": " + token.getToken());
        // 요청 URL도 함께 로깅
        System.out.println("Request URL: " + request.getRequestURL());
        return token;
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        if(token == null){
            return;
        }

        System.out.println("Token saving at " + LocalDateTime.now() + ": " + (token != null ? token.getToken() : "null"));
        delegate.saveToken(token, request, response);
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        CsrfToken token = delegate.loadToken(request);
        System.out.println("Token loaded at " + LocalDateTime.now() + ": " + (token != null ? token.getToken() : "null"));
        return token;
    }

}
