package org.kosa.myproject.dto;

import lombok.Builder;
import lombok.Getter;
import org.kosa.myproject.entity.Post;

import java.time.LocalDateTime;

/**
 * 게시글 목록 응답 DTO
 * - 목록 조회 시 필요한 간략한 정보만 포함
 * - content는 제외하여 데이터 전송량 최소화
 * - 페이징 처리 시 효율적
 */
@Getter
@Builder
public class PostListResponseDto {

    private Long id;                    // 게시글 번호

    private String title;               // 게시글 제목

    private String authorName;          // 작성자 이름 (username 아닌 실명)

    private LocalDateTime createdAt;    // 작성일시

    /**
     * Entity → DTO 변환 메서드
     * - 목록에 필요한 최소 정보만 추출
     * - N+1 문제 주의: fetch join 필요
     *
     * @param post 변환할 Post 엔티티
     * @return PostListResponseDto
     */
    public static PostListResponseDto from(Post post) {
        return PostListResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .authorName(post.getAuthor().getName())  // 작성자 실명
                .createdAt(post.getCreatedAt())
                .build();
    }
}