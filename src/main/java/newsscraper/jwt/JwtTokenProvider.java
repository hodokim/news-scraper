package newsscraper.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor // UserDetailsService 주입을 위해 추가
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-in-ms}")
    private long tokenValidityInMilliseconds;

    private final UserDetailsService userDetailsService; // Spring Security에서 사용자 정보를 가져오는 서비스

    private Key key;

    @PostConstruct
    protected void init() {
        key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT에서 인증 정보를 조회합니다.
     * @param token JWT
     * @return 인증 정보(Authentication)
     */
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsername(token));
        // UserDetails와 빈 권한 목록을 사용하여 Authentication 객체를 생성합니다.
        return new UsernamePasswordAuthenticationToken(userDetails, "", Collections.emptyList());
    }

    /**
     * 토큰에서 회원 구별 정보(username)를 추출합니다.
     * @param token JWT
     * @return 사용자 이름
     */
    public String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * 토큰의 유효성 + 만료일자를 확인합니다.
     * @param token JWT
     * @return 토큰 유효 여부
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // MalformedJwtException, ExpiredJwtException, UnsupportedJwtException, IllegalArgumentException
            log.error("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }
}