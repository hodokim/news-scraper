package newsscraper.domain; // 또는 enums 등 정의하신 패키지 경로

import lombok.Getter;

@Getter
public enum ScrapingSite {
    NAVER(
            "NAVER",
            "https://search.naver.com/search.naver?where=news&query=%s",
            "div.kYW833HRgWuWh92opkU9",                 // 뉴스 아이템 선택자
            "a.IQnq6B4xVFZbhCOvK9Fy",                   // 제목 선택자
            "span.sds-comps-text-type-body1",           // 요약 선택자
            "span.sds-comps-profile-info-title-sub-text" // 날짜 선택자
    ),
    DAUM(
            "DAUM",
            "https://search.daum.net/search?w=news&q=%s",
            "div.c-item-content",
            "div.item-title a",
            "p.conts-desc",
            "span.gem-subinfo"
    );

    // Getter
    private final String siteName;
    private final String urlFormat;
    private final String newsItemSelector;
    private final String titleSelector;
    private final String summarySelector;
    private final String dateSelector;

    ScrapingSite(String siteName, String urlFormat, String newsItemSelector, String titleSelector, String summarySelector, String dateSelector) {
        this.siteName = siteName;
        this.urlFormat = urlFormat;
        this.newsItemSelector = newsItemSelector;
        this.titleSelector = titleSelector;
        this.summarySelector = summarySelector;
        this.dateSelector = dateSelector;
    }

}