package newsscraper.dto;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class KeywordDTO {
    private Long id;
    private Long userId;
    private String keyword;
    private OffsetDateTime createdAt;
}