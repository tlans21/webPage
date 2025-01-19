package HomePage.config;

import HomePage.config.interceptor.DynamicEtagInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfig implements WebMvcConfigurer {



    // 응답 본문의 해시값을 계산해서 ETag 헤더를 생성 (응답 본문 전체를 etag 해시로 변환하기 때문에 커스텀 DynamicETagInterceptor에서 처리하기로 변경)
//    @Bean
//    public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
//        return new ShallowEtagHeaderFilter();
//    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 이미지 파일에 대한 설정
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS)
                    .cachePublic()
                    .mustRevalidate())
                .resourceChain(true);

        // 나머지 정적 리소스 설정
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS)
                    .cachePublic()
                    .mustRevalidate())
                .resourceChain(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DynamicEtagInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**", "/images/**");
    }
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false);
    }
}
