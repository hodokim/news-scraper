package newsscraper.service;

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
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsScrapingService {

    private final UserMapper userMapper;
    private final KeywordMapper keywordMapper;
    private final NewsService newsService;

    /**
     * 정기적으로 모든 키워드에 대해 뉴스를 스크래핑하는 스케줄링 메소드
     */
    @Scheduled(cron = "0 0 * * * *") // 매시 정각에 실행
    public void scrapeNewsPeriodically() {
        log.info("정기 뉴스 스크래핑 작업을 시작합니다.");

        List<UserDTO> users = userMapper.findAll();
        if (users.isEmpty()) {
            log.info("등록된 사용자가 없어 스크래핑을 종료합니다.");
            return;
        }

        for (UserDTO user : users) {
            List<KeywordDTO> keywords = keywordMapper.findKeywordsByUserId(user.getId());
            if (keywords.isEmpty()) {
                continue;
            }

            log.info("사용자 '{}'의 키워드에 대한 스크래핑을 시작합니다.", user.getUsername());
            for (KeywordDTO keyword : keywords) {
                // TODO: 사용자가 설정한 사이트만 스크래핑하도록 로직 추가 필요
                scrapeFromNaver(keyword);
                scrapeFromDaum(keyword);
            }
        }
        log.info("정기 뉴스 스크래핑 작업을 완료했습니다.");
    }

    /**
     * 특정 키워드에 대해 즉시 스크래핑을 수행하는 메소드 (비동기 실행)
     * @param keywordDto 스크래핑할 키워드 정보
     */
    @Async
    public void scrapeForKeyword(KeywordDTO keywordDto) {
        log.debug("키워드 '{}'에 대한 즉시 스크래핑을 시작합니다.", keywordDto.getKeyword());
        scrapeFromNaver(keywordDto);
        scrapeFromDaum(keywordDto);
        log.debug("키워드 '{}'에 대한 즉시 스크래핑을 완료했습니다.", keywordDto.getKeyword());
    }

    public void scrapeFromNaver(KeywordDTO keywordDto) {
        String site = "NAVER";
        try {
            String encodedKeyword = URLEncoder.encode(keywordDto.getKeyword(), StandardCharsets.UTF_8);
            String url = "https://search.naver.com/search.naver?where=news&query=" + encodedKeyword;

            // 인코딩된 URL을 함께 로깅하여 문제 확인
            log.debug("Scrape START {}", url);
            Document doc = Jsoup.connect(url).get();
            Elements newsElements = doc.select("div.kYW833HRgWuWh92opkU9");
            for (Element element : newsElements) {
                // 2. 각 세부 정보(제목, 링크, 언론사, 요약, 발행일)를 추출하는 선택자 변경
                Element titleAnchor = element.select("a.IQnq6B4xVFZbhCOvK9Fy").first();
                String title = (titleAnchor != null) ? titleAnchor.text() : "제목 없음";
                String link = (titleAnchor != null) ? titleAnchor.attr("href") : "링크 없음";

                Element pressElement = element.select("span.sds-comps-profile-info-title-text").first();
                String press = (pressElement != null) ? pressElement.text() : "언론사 없음";

                Element summaryElement = element.select("span.sds-comps-text-type-body1").first();
                String summary = (summaryElement != null) ? summaryElement.text() : "요약 없음";

                // 실제 발행일 파싱하여 설정
                Element dateElement = element.selectFirst("span.sds-comps-profile-info-title-sub-text");
                String dateString = (dateElement != null) ? dateElement.text() : null;

                NewsDTO news = new NewsDTO();
                news.setKeywordId(keywordDto.getId());
                news.setTitle(title);
                news.setLink(link);
                news.setSummary(summary);
                news.setSourceSite(site);
                news.setPublishedAt(parseNaverDate(dateString));
                newsService.saveNews(news);
            }
            log.debug("[{}] '{}' Scraping END", site, keywordDto.getKeyword());
        } catch (IOException e) {
            log.error("[{}] Scraping ERROR: {}", site, e.getMessage());
        }
    }

    private void scrapeFromDaum(KeywordDTO keywordDto) {
        String site = "DAUM";
        try {
            String encodedKeyword = URLEncoder.encode(keywordDto.getKeyword(), StandardCharsets.UTF_8.toString());
            String url = "https://search.daum.net/search?w=news&q=" + encodedKeyword;

            Document doc = Jsoup.connect(url).get();
            Elements newsElements = doc.select("ul.c-list-basic > li");

            for (Element element : newsElements) {
                Element titleElement = element.selectFirst("a.c-item-content");
                Element summaryElement = element.selectFirst("p.c-item-contents");

                if (titleElement != null && summaryElement != null) {
                    NewsDTO news = new NewsDTO();
                    news.setKeywordId(keywordDto.getId());
                    news.setTitle(titleElement.text());
                    news.setLink(titleElement.attr("href"));
                    news.setSummary(summaryElement.text());
                    news.setSourceSite(site);
                    news.setPublishedAt(OffsetDateTime.now()); // 실제 발행일 파싱 필요

                    newsService.saveNews(news);
                }
            }
            log.debug("Scrape END {}", url);
        } catch (IOException e) {
            log.debug("Scrape ERROR");
        }
    }

    private OffsetDateTime parseNaverDate(String dateString) {
        OffsetDateTime now = OffsetDateTime.now();
        try {
            if (dateString.contains("분 전")) {
                long minutesAgo = Long.parseLong(dateString.replaceAll("[^0-9]", ""));
                return now.minusMinutes(minutesAgo);
            } else if (dateString.contains("시간 전")) {
                long hoursAgo = Long.parseLong(dateString.replaceAll("[^0-9]", ""));
                return now.minusHours(hoursAgo);
            } else if (dateString.contains("일 전")) {
                long daysAgo = Long.parseLong(dateString.replaceAll("[^0-9]", ""));
                return now.minusDays(daysAgo);
            } else if (dateString.matches("\\d{4}\\.\\d{2}\\.\\d{2}\\.")) {
                dateString = dateString.substring(0, dateString.length() - 1);
                LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
                return date.atStartOfDay(now.getOffset()).toOffsetDateTime();
            }
        } catch (Exception e) {
            log.error("네이버 날짜 파싱 실패: '{}'", dateString, e);
        }
        return now;
    }
}
