package newsscraper.controller;

import newsscraper.dto.KeywordDTO;
import newsscraper.dto.NewsDTO;
import newsscraper.dto.UserDTO;
import newsscraper.service.KeywordService;
import newsscraper.service.NewsService;
import newsscraper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;
    private final KeywordService keywordService;
    private final UserService userService;

    /**
     * 현재 로그인된 사용자의 ID를 가져온다
     */
    private Long getCurrentUserId(Authentication authentication) {
        String username = authentication.getName();
        UserDTO user = userService.getUser(username);

        if (user == null) {
            // 토큰에 있는 사용자 이름이 DB에 없을 경우 예외 처리
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }
        return user.getId();
    }

    @GetMapping
    public ResponseEntity<List<NewsDTO>> getMyNews(Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication); // 현재 사용자 ID 조회
        List<NewsDTO> newsList = newsService.getNewsByUser(currentUserId);
        return ResponseEntity.ok(newsList);
    }

    @GetMapping("/keywords")
    public ResponseEntity<List<KeywordDTO>> getMyKeywords(Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication); // 현재 사용자 ID 조회
        List<KeywordDTO> keywords = keywordService.getKeywordsByUser(currentUserId);
        return ResponseEntity.ok(keywords);
    }

    @PostMapping("/keywords")
    public ResponseEntity<String> addKeyword(@RequestBody KeywordDTO keyword, Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication); // 현재 사용자 ID 조회
        keyword.setUserId(currentUserId);
        keywordService.addKeyword(keyword);
        return ResponseEntity.ok("키워드가 추가되었으며, 뉴스 수집을 시작합니다.");
    }

    @DeleteMapping("/keywords/{id}")
    public ResponseEntity<String> deleteKeyword(@PathVariable("id") Long keywordId) {
        //TODO: 삭제 요청에 대하여 현재 로그인한 사용자인지 확인하는 로직 추가
        keywordService.removeKeyword(keywordId);
        return ResponseEntity.ok("키워드가 삭제되었습니다.");
    }
}