package HomePage.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Value("${server.env}")
    private String env;
    @Value("${server.port}")
    private String serverPort;
    @Value("${server.serverAddress}")
    private String serverAddress;
    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfig = new CorsConfiguration();

//        if ("dev".equals(env)){
//            corsConfig.setAllowCredentials(true);
//    //        corsConfig.addAllowedOrigin("http://localhost:3000");
//            corsConfig.addAllowedOrigin("http://" + serverAddress);
//            corsConfig.addAllowedHeader("*");
//            corsConfig.addAllowedMethod("*");
//            corsConfig.addExposedHeader("Authorization");
//            source.registerCorsConfiguration("/**", corsConfig);
//
//        }
        corsConfig.setAllowCredentials(true);
 //        corsConfig.addAllowedOrigin("http://localhost:3000");
        corsConfig.addAllowedOrigin("http://" + serverAddress);
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedMethod("*");



        // 노출할 헤더 설정
        corsConfig.addExposedHeader("ETag");
        corsConfig.addExposedHeader("Cache-Control");
        corsConfig.addExposedHeader("If-None-Match");
        corsConfig.addExposedHeader("If-Match");

        source.registerCorsConfiguration("/**", corsConfig);


        return new CorsFilter(source);
    }

}
