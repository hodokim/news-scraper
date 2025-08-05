// UserController.java
package newsscraper.controller;

import newsscraper.dto.UserDTO;
import newsscraper.jwt.JwtTokenProvider; // JwtTokenProvider 주입
import newsscraper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager; // AuthenticationManager 주입
    private final JwtTokenProvider jwtTokenProvider;          // JwtTokenProvider 주입

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO user) {
        userService.registerUser(user);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDTO user) {
        // 1. AuthenticationManager에게 인증 위임
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        // 2. 인증 성공! 이제 JWT 생성
        String token = jwtTokenProvider.createToken(authentication.getName());

        // 3. 생성된 토큰을 클라이언트에 반환
        return ResponseEntity.ok(token);
    }
}