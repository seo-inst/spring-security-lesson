package org.kosa.myproject.dto;

import lombok.Builder;
import lombok.Getter;
import org.kosa.myproject.entity.Member;
import org.kosa.myproject.entity.MemberRole;

import java.time.LocalDateTime;

/**
 * 회원 정보 응답 DTO
 * - 서버 → 클라이언트로 전달되는 회원 정보
 * - 비밀번호 같은 민감한 정보는 제외
 * - 필요한 정보만 선택적으로 노출
 */
@Getter
@Builder
public class MemberResponseDto {

    private Long id;                    // 회원 번호 (PK)

    private String username;            // 로그인 ID

    private String name;                // 사용자 실명

    private MemberRole role;            // 권한

    private LocalDateTime createdAt;    // 가입일시

    /**
     * Entity → DTO 변환 메서드
     * - 정적 팩토리 메서드 패턴 사용
     * - 비밀번호는 응답에서 제외 (보안)
     *
     * @param member 변환할 Member 엔티티
     * @return MemberResponseDto
     */
    public static MemberResponseDto from(Member member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .name(member.getName())
                .role(member.getRole())
                .createdAt(member.getCreatedAt())
                .build();
    }
}