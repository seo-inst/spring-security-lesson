package org.kosa.myproject.dto;

import lombok.Builder;
import lombok.Getter;
import org.kosa.myproject.entity.Member;
import org.kosa.myproject.entity.Post;

import java.time.LocalDateTime;

/**
 * 게시글 상세 응답 DTO
 * - 게시글 상세 조회 시 모든 정보 포함
 * - 작성자 정보도 함께 제공
 * - 수정/삭제 권한 체크를 위한 authorId 포함
 */
@Getter
@Builder
public class PostDetailResponseDto {

    private Long id;                    // 게시글 번호

    private String title;               // 게시글 제목

    private String content;             // 게시글 내용 (상세 조회시만 포함)

    private Long authorId;              // 작성자 ID (권한 체크용)

    private String authorUsername;      // 작성자 로그인 ID

    private String authorName;          // 작성자 실명

    private LocalDateTime createdAt;    // 작성일시

    /**
     * Entity → DTO 변환 메서드
     * - 상세 정보 전체 포함
     * - 작성자 정보 함께 제공
     *
     * @param post 변환할 Post 엔티티
     * @return PostDetailResponseDto
     */
    public static PostDetailResponseDto from(Post post) {
        Member author = post.getAuthor();

        return PostDetailResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorId(author.getId())
                .authorUsername(author.getUsername())
                .authorName(author.getName())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
