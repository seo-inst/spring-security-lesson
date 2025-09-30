package org.kosa.myproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.myproject.dto.ApiResponseDto;
import org.kosa.myproject.dto.PostCreateRequestDto;
import org.kosa.myproject.dto.PostDetailResponseDto;
import org.kosa.myproject.dto.PostListResponseDto;
import org.kosa.myproject.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 커뮤니티 게시글 관리  REST API
 * <p>
 * 주요 엔드 포인트 :
 * GET /api/posts  : 전체 게시글 목록  ( 인증 불필요 )
 * GET /api/posts/{id} : 게시글 상세 조회 (인증 필요)
 * POST /api/posts : 게시글 작성 (인증 필요)
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;

    @GetMapping
    public ResponseEntity<?> getAllPosts() {
        log.info("게시물 리스트 조회 요청");
        List<PostListResponseDto> posts = postService.getAllPosts();
        log.info("게시물 리스트 조회 완료 {} 건", posts.size());
        // ApiResponseDto : 통일된 형식으로 응답하기 위한 Dto
        return ResponseEntity.ok(ApiResponseDto.success(posts, "게시물 목록 조회 성공"));
    }

    /**
     * 게시글 작성
     * 인증 필요 ( JWT 를 요청 헤더에 함께 전송 ) -> 로그인한 작성자만 등록 가능
     * <p>
     * POST 방식  /api/posts
     * raw JSON
     {
      "title":"Spring Security Study"
     "content":"JWT 기반 인증, 인가 처리"
      }
     * Spring Security 인증 흐름
     * 1) JWT 토큰 검증
     * 2) SecurityContext 에 인증 정보 저장
     * 3) SecurityContext에서 인증 정보 추출
     * 4) 작성자 정보와 함께 게시글 저장
     */
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostCreateRequestDto requestDto) {
        //SecurityContext 에서 현재 인증된 사용자 정보를 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 username 추출
        String username = authentication.getName();
        log.info("게시글 작성 , 작성자 {}", username);
        // 작성자 정보 포함
        PostDetailResponseDto postDetailResponseDto = postService.createPost(requestDto, username);
        // ApiResponseDto 의 표준화된 형식으로 응답한다
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(postDetailResponseDto, "게시글이 작성되었습니다"));
    }
    /**
     *  게시글 상세조회
     *  JWT 토큰 인증 필요 ( 로그인한 사용자만 작성 가능 )
     *  GET  http://localhost:8080/api/posts/1
     *
     * @param id  조회할 게시글 ID
     * @return  게시글 상세 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id){
        PostDetailResponseDto post = postService.getPostById(id);
        log.info("게시글 조회 성공 : title={}",post.getTitle());
        return ResponseEntity.ok(ApiResponseDto.success(post,"게시글 조회 성공"));
    }
}















