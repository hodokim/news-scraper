// src/main/java/newsscraper/service/NewsScrapingService.java

package newsscraper.service;

import newsscraper.domain.ScrapingSite;
import newsscraper.dto.KeywordDTO;
import newsscraper.dto.NewsDTO;
import newsscraper.dto.UserDTO;
import newsscraper.mapper.KeywordMapper;
import newsscraper.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsScrapingService {

    private final UserMapper userMapper;
    private final KeywordMapper keywordMapper;
    private final NewsService newsService;

    @Scheduled(cron = "0 0 * * * *")
    public void scrapeNewsPeriodically() {
        log.info("정기 뉴스 스크래핑 작업을 시작합니다.");

        List<UserDTO> users = userMapper.findAll();
        if (users.isEmpty()) {
            log.info("등록된 사용자가 없어 스크래핑을 종료합니다.");
            return;
        }
        // 각 사용자에 대한 스크래핑 작업 처리
        users.forEach(this::scrapeForUser);
        log.info("정기 뉴스 스크래핑 작업을 완료했습니다.");
    }

    /**
     * 특정 사용자 한 명에 대한 스크래핑 로직을 처리
     * @param user 스크래핑을 수행할 사용자 DTO
     */
    private void scrapeForUser(UserDTO user) {
        List<String> preferredSites = user.getPreferredSites();
        if (preferredSites == null || preferredSites.isEmpty()) {
            log.info("사용자 '{}' 에게 설정된 스크래핑 사이트가 없습니다.", user.getUsername());
            return;
        }

        List<KeywordDTO> keywords = keywordMapper.findKeywordsByUserId(user.getId());
        if (keywords.isEmpty()) {
            log.debug("사용자 '{}' 에게 등록된 키워드가 없습니다.", user.getUsername());
            return;
        }

        log.info("사용자 '{}'의 키워드에 대한 스크래핑을 시작합니다. (설정된 사이트: {})", user.getUsername(), preferredSites);
        keywords.forEach(keyword ->
                Arrays.stream(ScrapingSite.values())
                        .filter(site -> preferredSites.contains(site.name())) // 사용자가 선호하는 사이트만 필터링
                        .forEach(site -> scrapeFromSite(keyword, site)) // 각 사이트에 대해 스크래핑 실행
        );
    }

    /**
     * 특정 키워드에 대해 즉시 스크래핑을 수행하는 메소드 (비동기 실행)
     * @param keywordDto 스크래핑할 키워드 정보
     */
    @Async
    public void scrapeForKeyword(KeywordDTO keywordDto) {
        log.debug("키워드 '{}'에 대한 즉시 스크래핑을 시작합니다.", keywordDto.getKeyword());

        // 키워드를 등록한 사용자의 정보를 가져옵니다.
        UserDTO user = userMapper.findById(keywordDto.getUserId());
        if (user == null) {
            log.error("키워드에 해당하는 사용자를 찾을 수 없습니다. (UserId: {})", keywordDto.getUserId());
            return;
        }

        List<String> userPreferredSites = user.getPreferredSites();
        if (userPreferredSites == null || userPreferredSites.isEmpty()) {
            log.debug("사용자 '{}' 에게 설정된 스크래핑 사이트가 없어 즉시 스크래핑을 건너뜁니다.", user.getUsername());
            return;
        }

        log.debug("사용자 '{}'의 즉시 스크래핑을 시작합니다. (설정된 사이트: {})", user.getUsername(), userPreferredSites);
        // 사용자가 설정한 사이트에 대해서만 스크래핑을 수행합니다.
        for (ScrapingSite site : ScrapingSite.values()) {
            if (userPreferredSites.contains(site.name())) {
                scrapeFromSite(keywordDto, site);
            }
        }

        log.debug("키워드 '{}'에 대한 즉시 스크래핑을 완료했습니다.", keywordDto.getKeyword());
    }

    private void scrapeFromSite(KeywordDTO keywordDto, ScrapingSite site) {
        try {
            String encodedKeyword = URLEncoder.encode(keywordDto.getKeyword(), StandardCharsets.UTF_8);
            String url = String.format(site.getUrlFormat(), encodedKeyword);

            log.debug("[{}] Scrape START - URL: {}", site.getSiteName(), url);

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                    .get();

            Elements newsElements = doc.select(site.getNewsItemSelector());
            log.debug("[{}] Found {} news items.", site.getSiteName(), newsElements.size());

            for (Element element : newsElements) {
                Element titleElement = element.selectFirst(site.getTitleSelector());
                Element summaryElement = element.selectFirst(site.getSummarySelector());

                if (titleElement != null && summaryElement != null) {
                    NewsDTO news = new NewsDTO();
                    news.setKeywordId(keywordDto.getId());
                    news.setTitle(titleElement.text());
                    news.setLink(titleElement.attr("href"));
                    news.setSummary(summaryElement.text());
                    news.setSourceSite(site.getSiteName());
                    news.setPublishedAt(OffsetDateTime.now().withNano(0));
                    news.setCreatedAt(OffsetDateTime.now().withNano(0)); // 이 코드가 핵심입니다.
                    newsService.saveNews(news);
                }
            }
            log.debug("[{}] '{}' Scraping END", site.getSiteName(), keywordDto.getKeyword());

        } catch (IOException e) {
            log.error("[{}] Scraping ERROR for keyword '{}': {}", site.getSiteName(), keywordDto.getKeyword(), e.getMessage());
        }
    }
}