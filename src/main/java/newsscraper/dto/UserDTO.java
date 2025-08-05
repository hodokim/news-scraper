package newsscraper.dto;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List; // 추가

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private String email;
    private OffsetDateTime createdAt;
    private List<String> preferredSites; // 추가
}