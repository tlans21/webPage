package HomePage.config;

import HomePage.config.cache.CacheInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    CacheInterceptor cacheInterceptor;


    // 제외할 경로 패턴을 상수로 관리
    public static final List<String> CACHE_EXCLUDE_PATTERNS = Arrays.asList(
        "/api/**",
        "/profile/**",
        "/notifications/**"
    );

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(cacheInterceptor)
                .addPathPatterns("/**") // 모든 경로에 적용
                .excludePathPatterns(CACHE_EXCLUDE_PATTERNS); //API 경로는 캐싱할 이유가 없기 때문에 제외
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .mediaType("js", MediaType.valueOf("application/javascript"))
                .mediaType("css", MediaType.valueOf("text/css"));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**")
                      .addResourceLocations("classpath:/static/js/")
                      .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
                      .resourceChain(true);
        // CSS 리소스 핸들러 추가
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
                .resourceChain(true);
    }
}
