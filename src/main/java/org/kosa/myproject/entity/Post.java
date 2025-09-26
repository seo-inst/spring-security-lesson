package org.kosa.myproject.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

/**
 * Post 엔티티 - 게시글 관리
 *
 *  */
@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;  // 제목

    @Lob  // Large Object - TEXT 타입을
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;  // 내용

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;  // 작성일시

    @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name="author_id", nullable = false, foreignKey = @ForeignKey(name="fk_security_post_author"))
    @OnDelete(action = OnDeleteAction.CASCADE) // DB 차원의 ON DELETE CASCADE 와 동일
    private Member author;  // 작성자

    // ============ 연관관계 메서드 ============

    /**
     * 작성자 설정
     * - 게시글 생성 시 작성자 지정
     */
    public void assignAuthor(Member author) {
        this.author = author;
    }

    // ============ 비즈니스 메서드 ============

    /**
     * 게시글 수정
     * - 제목과 내용만 수정 가능
     * - 작성자와 작성일시는 변경 불가
     * - 입력값 검증 포함
     */
    public void updatePost(String title, String content) {
        // 제목 검증 및 수정
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
        // 내용 검증 및 수정
        if (content != null && !content.trim().isEmpty()) {
            this.content = content.trim();
        }
    }

    /**
     * 작성자 확인
     * - 수정/삭제 권한 체크에 사용
     * - Service 레이어에서 활용
     *
     * @param member 확인할 회원
     * @return 작성자 일치 여부
     */
    public boolean isAuthor(Member member) {
        return this.author != null &&
                this.author.getId().equals(member.getId());
    }
}