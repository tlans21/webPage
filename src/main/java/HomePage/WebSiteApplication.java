package HomePage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("HomePage.admin.mapper")
public class WebSiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebSiteApplication.class, args);
	}

}
