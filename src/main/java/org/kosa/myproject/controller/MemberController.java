package org.kosa.myproject.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.myproject.dto.ApiResponseDto;
import org.kosa.myproject.dto.MemberCreateRequestDto;
import org.kosa.myproject.dto.MemberResponseDto;
import org.kosa.myproject.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * MemberController - 회원 관리 REST API
 *
 * 역할: 회원가입, 로그인, 회원정보 조회 등의 HTTP 요청 처리
 *
 * 주요 엔드포인트:
 * - POST /api/members/register : 회원가입 (인증 불필요)
 * - POST /login : 로그인 (LoginFilter가 처리)
 * - GET /api/members/me : 내 정보 조회 (인증 필요)
 */
@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입 API
     * 누구나 접근 가능 (SecurityConfig에서 permitAll 설정 필요)
     * <p>
     * PostMan 테스트:
     * POST http://localhost:8080/api/members
     * Body (raw JSON):
     * {
     * "username": "user1",
     * "password": "1234",
     * "name": "손흥민"
     * }
     *
     * @param requestDto 회원가입 정보
     * @return 생성된 회원 정보
     */
    @PostMapping
    public ResponseEntity<?> register(@RequestBody MemberCreateRequestDto requestDto) {
        log.info("=== 회원가입 요청: username={} ===", requestDto.getUsername());

        // 예외는 GlobalExceptionHandler가 처리
        MemberResponseDto responseDto = memberService.register(requestDto);

        log.info("회원가입 성공: username={}", responseDto.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(responseDto, "회원가입이 완료되었습니다."));
    }

    /**
     * 내 정보 조회 API
     * JWT 토큰 인증 필요
     * <p>
     * PostMan 테스트:
     * GET http://localhost:8080/api/members/me
     * Headers:
     * - Authorization: Bearer {JWT_TOKEN}
     *
     *  // SecurityContext에서 현재 인증된 사용자 정보 추출
     *
     * @return 현재 로그인한 사용자 정보
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo() {
        log.info("=== 내 정보 조회 요청 ===");
        // SecurityContext에서 현재 인증된 사용자 정보 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("인증된 사용자 정보 조회: username={}", username);
        // 예외는 GlobalExceptionHandler가 처리
        MemberResponseDto memberInfo = memberService.getMyInfo(username);
        return ResponseEntity.ok(ApiResponseDto.success(memberInfo, "회원 정보 조회 OK"));
    }

    /**
     * Principal (CustomMemberDetails)객체를 직접 받아서 타입 캐스팅 없이 사용할 수 있어  편리
     */
//    @GetMapping("/me")
//    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal CustomMemberDetails memberDetails) {
//        log.info("=== 내 정보 조회 요청2 ===");
//        log.info("인증된 사용자 정보 조회: username={}", memberDetails.getUsername());
//        MemberResponseDto memberInfo = memberService.getMyInfo(memberDetails.getUsername());
//        return ResponseEntity.ok(ApiResponseDto.success(memberInfo, "회원 정보 조회 OK"));
//    }

    /**
     * 특정 회원 정보 조회 API (관리자용)
     * JWT 토큰 인증 + ADMIN 권한 필요
     * <p>
     * PostMan 테스트:
     * GET http://localhost:8080/api/members/{id}
     * Headers:
     * - Authorization: Bearer {JWT_TOKEN}
     *
     * @param id 조회할 회원 ID
     * @return 회원 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getMemberById(@PathVariable Long id) {
        log.info("=== 회원 정보 조회: id={} ===", id);

        // 예외는 GlobalExceptionHandler가 처리
        MemberResponseDto member = memberService.findById(id);

        return ResponseEntity.ok(ApiResponseDto.success(member, "회원 정보 조회 OK"));
    }

}
