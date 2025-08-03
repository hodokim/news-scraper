package newsscraper.controller;

import newsscraper.dto.KeywordDTO;
import newsscraper.service.NewsScrapingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final NewsScrapingService newsScrapingService;

    /**
     * Naver 뉴스 스크래핑 기능을 테스트하기 위한 임시 API
     * @param keyword 검색할 키워드
     * @return 성공 메시지
     */
    @GetMapping("/scrape/naver")
    public ResponseEntity<String> testScrapeNaver(@RequestParam String keyword) {
        // 테스트 목적이므로 실제 DB에 저장된 키워드가 아니어도 됩니다.
        // 임시 KeywordDTO를 생성하여 스크래핑 서비스를 호출합니다.
        KeywordDTO testKeyword = new KeywordDTO();
        testKeyword.setId(0L); // 임시 ID
        testKeyword.setUserId(0L); // 임시 사용자 ID
        testKeyword.setKeyword(keyword);

        // 스크래핑 서비스의 특정 메소드를 직접 호출
        newsScrapingService.scrapeFromNaver(testKeyword);

        return ResponseEntity.ok("Naver scraping test initiated for keyword: " + keyword);
    }
}
