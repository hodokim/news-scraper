package newsscraper.dto;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private String email;
    private OffsetDateTime createdAt;
}

