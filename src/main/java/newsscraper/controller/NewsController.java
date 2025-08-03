package newsscraper.controller;

import newsscraper.dto.KeywordDTO;
import newsscraper.dto.NewsDTO;
import newsscraper.service.KeywordService;
import newsscraper.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;
    private final KeywordService keywordService;

    @GetMapping
    public ResponseEntity<List<NewsDTO>> getMyNews() {
        Long currentUserId = 5L; // 임시
        List<NewsDTO> newsList = newsService.getNewsByUser(currentUserId);
        return ResponseEntity.ok(newsList);
    }

    @GetMapping("/keywords")
    public ResponseEntity<List<KeywordDTO>> getMyKeywords() {
        Long currentUserId = 5L; // 임시
        List<KeywordDTO> keywords = keywordService.getKeywordsByUser(currentUserId);
        return ResponseEntity.ok(keywords);
    }

    @PostMapping("/keywords")
    public ResponseEntity<String> addKeyword(@RequestBody KeywordDTO keyword) {
        Long currentUserId = 5L; // 임시
        keyword.setUserId(currentUserId);
        keywordService.addKeyword(keyword);
        return ResponseEntity.ok("키워드가 추가되었으며, 뉴스 수집을 시작합니다.");
    }

    @DeleteMapping("/keywords/{id}")
    public ResponseEntity<String> deleteKeyword(@PathVariable("id") Long keywordId) {
        keywordService.removeKeyword(keywordId);
        return ResponseEntity.ok("키워드가 삭제되었습니다.");
    }
}