package org.kosa.myproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 작성 요청 DTO
 * - 클라이언트 → 서버로 전달되는 게시글 작성 정보
 * - 작성자는 Spring Security의 인증 정보에서 자동 추출
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateRequestDto {

    private String title;    // 게시글 제목

    private String content;  // 게시글 내용

    // authorId는 별도로 받지 않음
    // - Spring Security에서 현재 로그인한 사용자 정보 사용
    // - SecurityContextHolder.getContext().getAuthentication()
    // - 보안상 더 안전한 방식
}