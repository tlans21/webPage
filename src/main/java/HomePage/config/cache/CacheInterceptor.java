package HomePage.config.cache;

import HomePage.config.WebConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

@Component
public class CacheInterceptor implements HandlerInterceptor {
    @Autowired
    private PathMatcher pathMatcher;
    private boolean isExcludedPath(String requestUri) {
         return WebConfig.CACHE_EXCLUDE_PATTERNS.stream()
                 .anyMatch(pattern -> pathMatcher.match(pattern, requestUri));
    }
    // 뷰 캐싱을 위해서, 컨트롤러 실행 후, 페이지 렌더링 이전의 postHandle로 가로채야함
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        String requestUri = request.getRequestURI();

        // 설정된 제외 경로에는 BYPASS로 캐싱하지않는다.
        if (isExcludedPath(requestUri)) {
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("X-Cache-Status", "BYPASS");
            return;
        }
        // 로그인 여부에 따른 캐시 정책 설정
        if (request.getCookies() != null && Arrays.stream(request.getCookies())
                .anyMatch(cookie -> "access_token".equals(cookie.getName()))) {
            response.setHeader("Cache-Control", "private, max-age=600");
        } else {
            response.setHeader("Cache-Control", "public, max-age=600");
        }
        response.setHeader("X-Cache-Status", "HIT");
    }
}
