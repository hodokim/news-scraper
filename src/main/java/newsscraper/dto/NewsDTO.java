package newsscraper.dto;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class NewsDTO {
    private Long id;
    private Long keywordId;
    private String title;
    private String link;
    private String summary;
    private OffsetDateTime publishedAt;
    private String sourceSite;
    private OffsetDateTime createdAt;
}