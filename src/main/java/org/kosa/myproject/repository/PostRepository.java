package org.kosa.myproject.repository;

import org.kosa.myproject.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Post Repository
 * N+1 문제를 해결하는 Fetch Join 쿼리 포함
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    /**
     * 모든 게시글을 작성자 정보와 함께 조회
     *
     * - N+1 문제 방지 (fetch join으로 한 번에 조회)
     * - 게시글 목록을 보여줄 때 작성자 이름도 필요
     */
    @Query("SELECT p FROM Post p JOIN FETCH p.author ORDER BY p.createdAt DESC")
    List<Post> findAllWithAuthor();

    /**
     * 특정 게시글을 작성자 정보와 함께 조회
     *
     * fetch join을 사용하는 이유:
     * - 게시글 상세 보기에서 작성자 정보가 필요
     * - Lazy Loading으로 인한 추가 쿼리 방지
     *
     * @param id 게시글 ID
     * @return Optional<Post>
     */
    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.id = :id")
    Optional<Post> findByIdWithAuthor(Long id);
}




