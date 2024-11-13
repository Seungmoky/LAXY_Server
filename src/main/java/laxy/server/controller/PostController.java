package laxy.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import laxy.server.dto.request.CreatePostRequest;
import laxy.server.dto.request.UpdatePostRequest;
import laxy.server.dto.response.PostDetailResponse;
import laxy.server.dto.response.PostSimpleResponse;
import laxy.server.service.PostService;
import laxy.server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "게시글 관련 API")
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 등록")
    @PostMapping("/post") // 게시글 등록
    public ResponseEntity<Long> createPost(@RequestHeader("Authorization") String token, @Valid @RequestBody CreatePostRequest request) {
        Long memberId = JwtUtil.extractAccessToken(token);
        Long postId = postService.createPost(memberId, request);
        return ResponseEntity.ok(postId);
    }

    @Operation(summary = "게시글 수정")
    @PutMapping("/post/{post_id}") // 게시글 수정
    public ResponseEntity<String> updatePost(@RequestHeader("Authorization") String token, @PathVariable Long post_id, @Valid @RequestBody UpdatePostRequest request) {
        Long memberId = JwtUtil.extractAccessToken(token);
        postService.updatePost(memberId, post_id, request);
        return ResponseEntity.ok("success");
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/post/{post_id}") // 게시글 삭제
    public ResponseEntity<String> deletePost(@RequestHeader("Authorization") String token, @PathVariable Long post_id) {
        Long memberId = JwtUtil.extractAccessToken(token);
        postService.deletePost(memberId, post_id);
        return ResponseEntity.ok("success");
    }

    @Operation(summary = "[커뮤니티]일간 인기 게시글 조회(조회수 기반 상위 10개)")
    @GetMapping("/daily/{tag_name}") // 일간 인기 게시글
    public ResponseEntity<List<PostSimpleResponse>> getDailyPopularPosts(@PathVariable("tag_name") String tagName) {
        List<PostSimpleResponse> posts = postService.getDailyPopularPosts(tagName);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "[커뮤니티]주간 인기 게시글 조회(조회수 기반 상위 10개)")
    @GetMapping("/weekly/{tag_name}") // 주간 인기 게시글
    public ResponseEntity<List<PostSimpleResponse>> getWeeklyPopularPosts(@PathVariable("tag_name") String tagName) {
        List<PostSimpleResponse> posts = postService.getWeeklyPopularPosts(tagName);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "[커뮤니티]공감 게시글 조회(좋아요 5개 이상 게시물, 내림차순)")
    @GetMapping("/liked/{tag_name}") // 공감 글 (좋아요 5개 이상)
    public ResponseEntity<List<PostSimpleResponse>> getLikedPosts(@PathVariable("tag_name") String tagName) {
        List<PostSimpleResponse> posts = postService.getLikedPosts(tagName);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "[태그/커뮤니티]전체 게시글 조회. sortBy 옵션: 최신순(recent), 조회순(views), 좋아요순(likes), 기본 값은 recent")
    @GetMapping("/all/{tag_name}") // 전체 게시글 조회
    public ResponseEntity<List<PostSimpleResponse>> getAllPostsByTag(
            @PathVariable("tag_name") String tagName,
            @RequestParam(value = "sortBy", defaultValue = "recent") String sortBy) {
        List<PostSimpleResponse> posts = postService.getAllPostsByTag(tagName, sortBy);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "게시글 상세 조회")
    @GetMapping("/post/{post_id}") // 게시글 상세 조회
    public ResponseEntity<PostDetailResponse> getPostDetail(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable Long post_id) {
        Long memberId = null;
        if (token != null && !token.isEmpty()) { memberId = JwtUtil.extractAccessToken(token); }
        PostDetailResponse postDetail = postService.getPostDetail(memberId, post_id);
        return ResponseEntity.ok(postDetail);
    }

    @Operation(summary = "[게시글 상세]게시글 좋아요 등록")
    @PostMapping("/post/like/{post_id}") // 게시글 좋아요 등록
    public ResponseEntity<String> likePost(@RequestHeader("Authorization") String token, @PathVariable Long post_id) {
        Long memberId = JwtUtil.extractAccessToken(token);
        postService.likePost(memberId, post_id);
        return ResponseEntity.ok("success");
    }

    @Operation(summary = "[게시글 상세]게시글 좋아요 해제")
    @DeleteMapping("/post/like/{post_id}") // 게시글 좋아요 해제
    public ResponseEntity<String> unlikePost(@RequestHeader("Authorization") String token, @PathVariable Long post_id) {
        Long memberId = JwtUtil.extractAccessToken(token);
        postService.unlikePost(memberId, post_id);
        return ResponseEntity.ok("success");
    }

    @Operation(summary = "[메인/트렌드]전체 게시글 일간 인기 게시글 조회(조회수 기반 상위 10개)")
    @GetMapping("/daily") // 전체 일간 인기 게시글
    public ResponseEntity<List<PostSimpleResponse>> getDailyPopularPosts() {
        List<PostSimpleResponse> posts = postService.getDailyPopularPosts();
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "[메인/트렌드]전체 게시글 주간 인기 게시글 조회(조회수 기반 상위 10개)")
    @GetMapping("/weekly") // 전체 주간 인기 게시글
    public ResponseEntity<List<PostSimpleResponse>> getWeeklyPopularPosts() {
        List<PostSimpleResponse> posts = postService.getWeeklyPopularPosts();
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "[메인/트렌드]전체 게시글 조회. sortBy 옵션: 최신순(recent), 조회순(views), 좋아요순(likes), 기본 값은 recent")
    @GetMapping("/all") // 전체 게시글 조회
    public ResponseEntity<List<PostSimpleResponse>> getAllPosts(
            @RequestParam(value = "sortBy", defaultValue = "recent") String sortBy) {
        List<PostSimpleResponse> posts = postService.getAllPosts(sortBy);
        return ResponseEntity.ok(posts);
    }
}
