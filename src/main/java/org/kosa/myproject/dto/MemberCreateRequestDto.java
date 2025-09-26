package org.kosa.myproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 가입 요청 DTO
 * - 클라이언트 → 서버로 전달되는 회원가입 정보
 * - Entity와 분리하여 계층 간 의존성 감소
 * - 검증 로직은 Service 레이어에서 처리
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCreateRequestDto {

    private String username;  // 로그인 ID (Entity 필드명과 일치)

    private String password;  // 비밀번호 (암호화 전)

    private String name;      // 사용자 실명

    // role은 기본값(ROLE_USER)으로 자동 설정되므로 요청에서 제외
}