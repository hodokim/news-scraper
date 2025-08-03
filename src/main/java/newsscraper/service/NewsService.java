package newsscraper.service;

import lombok.extern.slf4j.Slf4j;
import newsscraper.dto.NewsDTO;
import newsscraper.mapper.NewsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsMapper newsMapper;

    public List<NewsDTO> getNewsByUser(Long userId) {
        return newsMapper.findNewsByUserId(userId);
    }

    public void saveNews(NewsDTO news) {
        // 중복된 링크가 없으면 DB에 저장하고 로그를 남깁니다.
        if (newsMapper.countByLink(news.getLink()) == 0) {
            newsMapper.insertNews(news);
            log.debug("  -> DB SAVE END: {}", news.getTitle());
        } else {
            // 중복된 링크가 있으면, 중복되었다는 로그를 남깁니다.
            log.debug("  -> DUPLICATED NEWS: {}", news.getTitle());
        }
    }
}
