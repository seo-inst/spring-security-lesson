package org.kosa.myproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.myproject.dto.PostCreateRequestDto;
import org.kosa.myproject.dto.PostDetailResponseDto;
import org.kosa.myproject.dto.PostListResponseDto;
import org.kosa.myproject.entity.Member;
import org.kosa.myproject.entity.Post;
import org.kosa.myproject.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Post Service
 * 게시물 관련 비즈니스 로직 + N+1 문제 해결
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postRepository;
    private final MemberService memberService;
    /**
     * 1. 전체 게시글 목록 조회
     *
     * @return 게시글 목록 (간략한 정보만 포함)
     */
    public List<PostListResponseDto> getAllPosts() {
        log.info("=== 게시글 목록 조회 시작 ===");

        // fetch join으로 작성자 정보까지 한 번에 조회
        List<Post> posts = postRepository.findAllWithAuthor();

        log.info("조회된 게시글 수: {}", posts.size());

        // Entity → DTO 변환
        return posts.stream()
                .map(PostListResponseDto::from)  // 메서드 레퍼런스
                .collect(Collectors.toList());
    }

    /**
     * 2. 게시글 상세 조회
     *
     * @param postId 조회할 게시글 ID
     * @return 게시글 상세 정보
     * @throws IllegalArgumentException 게시글이 없을 경우
     */
    public PostDetailResponseDto getPostById(Long postId) {
        log.info("=== 게시글 상세 조회: id={} ===", postId);

        // 게시글 조회 (작성자 정보 포함)
        Post post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> {
                    log.error("게시글 조회 실패: id={}", postId);
                    return new IllegalArgumentException("존재하지 않는 게시글입니다.");
                });

        log.info("게시글 조회 성공: title={}", post.getTitle());

        return PostDetailResponseDto.from(post);
    }

    /**
     * 3. 게시글 작성
     *
     * @param requestDto 게시글 작성 정보
     * @param username 현재 로그인한 사용자 (Spring Security에서 추출)
     * @return 생성된 게시글 상세 정보
     */
    @Transactional  // 쓰기 작업 - 트랜잭션 필수!
    public PostDetailResponseDto createPost(PostCreateRequestDto requestDto,
                                            String username) {
        log.info("=== 게시글 작성 시작: username={} ===", username);

        // 1. 작성자 정보 조회
        Member author = memberService.getMemberEntity(username);

        // 2. Post 엔티티 생성
        Post post = Post.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .author(author)  // 작성자 설정
                .build();

        // 3. 저장
        Post savedPost = postRepository.save(post);
        log.info("게시글 작성 완료: id={}, title={}",
                savedPost.getId(), savedPost.getTitle());

        // 4. DTO 변환 후 반환
        return PostDetailResponseDto.from(savedPost);
    }

    /**
     * 4. 작성자 권한 확인 (추후 수정/삭제 시 사용)
     *
     * @param postId 게시글 ID
     * @param username 확인할 사용자
     * @return true: 작성자, false: 작성자 아님
     */
    public boolean isAuthor(Long postId, String username) {
        Post post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 게시글입니다."));

        return post.getAuthor().getUsername().equals(username);
    }
}







