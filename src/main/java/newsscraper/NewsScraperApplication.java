package newsscraper;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync // 비동기 기능 활성화
@EnableScheduling
@SpringBootApplication
@MapperScan("newsscraper.mapper")
public class NewsScraperApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsScraperApplication.class, args);
    }

}
