package newsscraper.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 요청 헤더에서 JWT를 추출
        String token = resolveToken(request);

        // 2. 토큰이 유효한지 검증
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰이 유효하면, 토큰으로부터 인증 정보(Authentication)를 받아옴
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            // SecurityContext에 인증 정보를 저장
            // 이 시점부터 해당 요청을 처리하는 동안 사용자는 인증된 상태가 됨
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터로 제어를 넘김
        filterChain.doFilter(request, response);
    }

    /**
     * 요청 헤더에서 토큰 정보를 추출하는 메소드
     * @param request HttpServletRequest 객체
     * @return 추출된 토큰 문자열
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}