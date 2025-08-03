package newsscraper.service;

import newsscraper.dto.KeywordDTO;
import newsscraper.mapper.KeywordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordMapper keywordMapper;
    private final NewsScrapingService newsScrapingService; // 스크래핑 서비스 주입

    @Transactional
    public void addKeyword(KeywordDTO keyword) {
        // 1. 키워드를 데이터베이스에 저장
        keywordMapper.insertKeyword(keyword);

        // 2. 저장된 키워드에 대해 즉시 스크래핑을 비동기적으로 실행
        // (insertKeyword 실행 시 useGeneratedKeys에 의해 keyword DTO에 id가 채워짐)
        newsScrapingService.scrapeForKeyword(keyword);
    }

    public List<KeywordDTO> getKeywordsByUser(Long userId) {
        return keywordMapper.findKeywordsByUserId(userId);
    }

    public void removeKeyword(Long keywordId) {
        keywordMapper.deleteKeyword(keywordId);
    }
}
