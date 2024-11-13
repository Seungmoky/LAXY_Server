package laxy.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import laxy.server.dto.request.CreateCommentRequest;
import laxy.server.dto.response.CommentResponse;
import laxy.server.service.CommentService;
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
@Tag(name = "댓글 관련 API")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "[게시글 상세]해당 게시물 댓글 목록 조회. sortBy 옵션: 인기순(popular), 최신순(recent), 내가 작성한 댓글(my), 기본 값은 recent")
    @GetMapping("/comment/{post_id}") // 댓글 목록 조회
    public ResponseEntity<List<CommentResponse>> getComments(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long post_id,
            @RequestParam(defaultValue = "recent") String sortBy
    ) {

        Long memberId = (token != null) ? JwtUtil.extractAccessToken(token) : null;
        List<CommentResponse> comments;

        switch (sortBy.toLowerCase()) {
            case "popular":
                comments = commentService.getCommentsByPopularity(post_id, memberId);
                break;
            case "my":
                comments = commentService.getCommentsByMy(post_id, memberId);
                break;
            case "recent":
            default:
                comments = commentService.getCommentsByRecent(post_id, memberId);
                break;
        }

        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "[게시글 상세]댓글 작성")
    @PostMapping("/comment/{post_id}") // 댓글 작성
    public ResponseEntity<String> createComment(@RequestHeader("Authorization") String token, @PathVariable Long post_id, @Valid @RequestBody CreateCommentRequest request) {
        Long memberId = JwtUtil.extractAccessToken(token);
        commentService.createComment(memberId, post_id, request);
        return ResponseEntity.ok("success");
    }

    @Operation(summary = "[게시글 상세]댓글 수정")
    @PutMapping("/comment/{comment_id}") // 댓글 수정
    public ResponseEntity<String> updateComment(@RequestHeader("Authorization") String token, @PathVariable Long comment_id, @Valid @RequestBody CreateCommentRequest request) {
        Long memberId = JwtUtil.extractAccessToken(token);
        commentService.updateComment(memberId, comment_id, request);
        return ResponseEntity.ok("success");
    }

    @Operation(summary = "[게시글 상세]댓글 삭제")
    @DeleteMapping("/comment/{comment_id}") // 댓글 삭제
    public ResponseEntity<String> deleteComment(@RequestHeader("Authorization") String token, @PathVariable Long comment_id) {
        Long memberId = JwtUtil.extractAccessToken(token);
        commentService.deleteComment(memberId, comment_id);
        return ResponseEntity.ok("success");
    }

    @Operation(summary = "[게시글 상세]댓글 좋아요 등록")
    @PostMapping("/comment/like/{comment_id}") // 댓글 좋아요 등록
    public ResponseEntity<String> likeComment(@RequestHeader("Authorization") String token, @PathVariable Long comment_id) {
        Long memberId = JwtUtil.extractAccessToken(token);
        commentService.likeComment(memberId, comment_id);
        return ResponseEntity.ok("success");
    }

    @Operation(summary = "[게시글 상세]댓글 좋아요 해제")
    @DeleteMapping("/comment/like/{comment_id}") // 댓글 좋아요 해제
    public ResponseEntity<String> unlikeComment(@RequestHeader("Authorization") String token, @PathVariable Long comment_id) {
        Long memberId = JwtUtil.extractAccessToken(token);
        commentService.unlikeComment(memberId, comment_id);
        return ResponseEntity.ok("success");
    }
}