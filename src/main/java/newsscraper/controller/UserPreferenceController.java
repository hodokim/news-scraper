package newsscraper.controller;

import lombok.RequiredArgsConstructor;
import newsscraper.domain.ScrapingSite;
import newsscraper.dto.UserDTO;
import newsscraper.service.UserPreferenceService;
import newsscraper.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserPreferenceController {

    private final UserPreferenceService userPreferenceService;
    private final UserService userService;

    private Long getCurrentUserId(Authentication authentication) {
        String username = authentication.getName();
        UserDTO user = userService.getUser(username);

        if (user == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }
        return user.getId();
    }

    /**
     * 로그인된 사용자 본인의 정보를 조회
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyInfo(Authentication authentication) {
        UserDTO user = userService.getUser(authentication.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        // 응답 시 비밀번호는 보내지 않도록 null 처리
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    /**
     * 사용자의 선호 스크래핑 사이트를 추가
     */
    @PostMapping("/preferences/sites")
    public ResponseEntity<String> addPreferredSite(@RequestBody Map<String, String> payload, Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        String siteName = payload.get("siteName");
        // siteName 유효성 검증 로직 추가
        if (siteName == null || siteName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("siteName을 입력해주세요.");
        }
        try {
            // 입력된 siteName이 ScrapingSite Enum에 존재하는지 확인 (대소문자 무시)
            ScrapingSite.valueOf(siteName.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Enum에 존재하지 않으면 예외 발생
            return ResponseEntity.badRequest().body("유효하지 않은 사이트 이름입니다: " + siteName);
        }

        userPreferenceService.addPreferredSite(currentUserId, siteName.toUpperCase());
        return ResponseEntity.ok("선호 사이트가 추가되었습니다.");
    }

    /**
     * 사용자의 선호 스크래핑 사이트를 삭제
     */
    @DeleteMapping("/preferences/sites/{siteName}")
    public ResponseEntity<String> removePreferredSite(@PathVariable String siteName, Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        // siteName 유효성 검증 로직 추가
        try {
            // 입력된 siteName이 ScrapingSite Enum에 존재하는지 확인 (대소문자 무시)
            ScrapingSite.valueOf(siteName.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Enum에 존재하지 않으면 예외 발생
            return ResponseEntity.badRequest().body("유효하지 않은 사이트 이름입니다: " + siteName);
        }

        userPreferenceService.removePreferredSite(currentUserId, siteName.toUpperCase());
        return ResponseEntity.ok("선호 사이트가 삭제되었습니다.");
    }
}