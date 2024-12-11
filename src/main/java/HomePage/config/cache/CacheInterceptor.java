package HomePage.config.cache;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

@Component
public class CacheInterceptor implements HandlerInterceptor {


    // 뷰 캐싱을 위해서, 컨트롤러 실행 후, 페이지 렌더링 이전의 postHandle로 가로채야함
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 로그인 여부에 따른 캐시 정책 설정
        if (request.getCookies() != null && Arrays.stream(request.getCookies())
                .anyMatch(cookie -> "access_token".equals(cookie.getName()))) {
            // 로그인된 사용자용 캐시 설정
            response.setHeader("Cache-Control", "private, max-age=0");
        } else {
            // 비로그인 사용자용 캐시 설정
            response.setHeader("Cache-Control", "public, max-age=600");
        }
    }
}
