package HomePage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfig implements WebMvcConfigurer {



    // 응답 본문의 해시값을 계산해서 ETag 헤더를 생성
    @Bean
    public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }


    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .mediaType("js", MediaType.valueOf("application/javascript"))
                .mediaType("css", MediaType.valueOf("text/css"));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 모든 정적 리소스에 대한 공통 캐시 정책 적용
        registry.addResourceHandler("/**")
                    .addResourceLocations("classpath:/static/")
                    .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS)
                        .cachePublic()
                        .mustRevalidate())
                    .resourceChain(true);

        // 이미지 파일에 대한 추가 설정
        registry.addResourceHandler("/**/*.jpg", "/**/*.jpeg", "/**/*.png", "/**/*.gif")
                .addResourceLocations("classpath:/static/images/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS)
                    .cachePublic()
                    .mustRevalidate())
                .resourceChain(true);
    }
}
