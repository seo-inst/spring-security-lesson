package org.kosa.myproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.kosa.myproject.dto.ApiResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AdminController - 관리자 전용 API
 *
 * 역할: ROLE_ADMIN 권한을 가진 사용자만 접근 가능한 엔드포인트
 *
 * Spring Security 권한 검증 흐름:
 * 1. JWT 토큰 검증 (JwtAuthenticationFilter)
 * 2. 토큰에서 권한 정보(ROLE_ADMIN) 추출
 * 3. Authentication 객체를 SecurityContext에 저장
 * 4. AuthorizationFilter에서 권한 확인
 * 5. ROLE_ADMIN 권한이 있으면 접근 허용, 없으면 AccessDenied
 */
@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    /**
     * 관리자 정보 조회
     * ROLE_ADMIN 권한 필요 (SecurityConfig에서 설정)
     *
     * Postman 테스트:
     * GET http://localhost:8080/api/admin
     * Headers:
     *   - Authorization: Bearer {JWT_TOKEN}
     *   (토큰은 ROLE_ADMIN 권한을 가진 사용자의 토큰이어야 함)
     *
     * 실패 시 (권한 없음):
     * CustomAccessDeniedHandler가 실행되어 403 Forbidden 반환
     *
     * @param authentication Spring Security가 주입하는 인증 정보
     * @return 관리자 정보 및 권한 목록
     */
    @GetMapping
    public ResponseEntity<?> getAdminInfo(Authentication authentication) {
        log.info("=== 관리자 페이지 접근: username={} ===",
                authentication.getName());

        // 현재 인증된 사용자 정보 추출
        String username = authentication.getName();

        // 권한 목록을 문자열 리스트로 변환
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)  // 메서드 레퍼런스 사용
                .collect(Collectors.toList());

        log.info("사용자 권한 목록:");
        roles.forEach(role -> log.info("  - {}", role));

        // 응답 데이터 구성
        Map<String, Object> adminInfo = new HashMap<>();
        adminInfo.put("username", username);
        adminInfo.put("roles", roles);
        adminInfo.put("message", "관리자 페이지에 접근하셨습니다.");

        // ApiResponseDto로 일관된 응답 형식 제공
        return ResponseEntity.ok(
                ApiResponseDto.success(adminInfo, "관리자 정보 조회 성공")
        );
    }

}
