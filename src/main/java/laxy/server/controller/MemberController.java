package laxy.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import laxy.server.dto.request.LoginRequest;
import laxy.server.dto.request.SignUpRequest;
import laxy.server.dto.request.UpdateMemberRequest;
import laxy.server.dto.response.CommentResponse;
import laxy.server.dto.response.LoginResponse;
import laxy.server.dto.response.PostSimpleResponse;
import laxy.server.dto.response.TagResponse;
import laxy.server.service.CommentService;
import laxy.server.service.MemberService;
import laxy.server.service.PostService;
import laxy.server.service.TagService;
import laxy.server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "회원 및 마이페이지 관련 API")
public class MemberController {

    private final MemberService memberService;
    private final TagService tagService;
    private final PostService postService;
    private final CommentService commentService;

    @Operation(summary = "[API 연동 테스트 용]현재 시간 조회")
    @GetMapping("/time")
    public ResponseEntity<String> getCurrentTime() {
        // 현재 시간을 가져와서 특정 형식으로 포맷
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNow = now.format(formatter);

        return ResponseEntity.ok(formattedNow);
    }

    @Operation(summary = "[로그인 전]로그인")
    @PostMapping("/login") // 로그인
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(memberService.login(request));
    }

    @Operation(summary = "[로그인 전]회원 가입")
    @PostMapping("/signUp") // 회원 가입
    public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRequest request) {
        memberService.signUp(request);
        return ResponseEntity.ok("success");
    }

    @Operation(summary = "[마이페이지]회원 정보 수정(값이 있으면 수정합니다. 수정사항 없는 항목은 빈 값을 넣어주세요.)")
    @PutMapping("/user") // 회원 정보 수정
    public ResponseEntity<String> updateMember(@RequestHeader("Authorization") String token, @RequestBody UpdateMemberRequest request) {
        Long memberId = JwtUtil.extractAccessToken(token);
        memberService.updateMember(memberId, request);
        return ResponseEntity.ok("success");
    }

    @Operation(summary = "[마이페이지]회원 탈퇴")
    @DeleteMapping("/user") // 회원 탈퇴
    public ResponseEntity<String> deleteMember(@RequestHeader("Authorization") String token) {
        Long memberId = JwtUtil.extractAccessToken(token);
        memberService.deleteMember(memberId);
        return ResponseEntity.ok("success");
    }

    @Operation(summary = "[엑세스 토큰 만료 시]리프레시 토큰으로 엑세스 토큰 재발급(엑세스 토큰 만료 기간: 30분)")
    @GetMapping("/refreshAccessToken") // 리프레시 토큰 받아서 엑세스 토큰 반환
    public ResponseEntity<String> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
        Long memberId = JwtUtil.extractAccessToken(refreshToken);
        return ResponseEntity.ok(memberService.refreshAccessToken(memberId));
    }

    @Operation(summary = "[로그인 전][드로워]최대 5개의 추천 커뮤니티 조회(현재 랜덤으로 조회)")
    @GetMapping("/randomTags") // 로그인 전 랜덤으로 5개의 커뮤니티 태그 가져오기
    public ResponseEntity<List<TagResponse>> getRandomCommunityTags() {
        List<TagResponse> randomTags = tagService.getRandomCommunityTags();
        return ResponseEntity.ok(randomTags);
    }

    @Operation(summary = "[로그인 후][드로워]최대 5개의 북마크한 태그 조회(최신순)")
    @GetMapping("/myTags") // 최근 북마크한 최대 5개의 태그 목록
    public ResponseEntity<List<TagResponse>> getMyRecentTags(@RequestHeader("Authorization") String token) {
        Long memberId = JwtUtil.extractAccessToken(token);
        List<TagResponse> myTags = tagService.getMyRecentTags(memberId);
        return ResponseEntity.ok(myTags);
    }

    @Operation(summary = "[로그인 후][드로워]북마크한 태그 기반 연관도 높은 최대 5개의 추천 커뮤니티 조회")
    @GetMapping("/recommendedTags") // 북마크한 태그 기반 연관도 높은 최대 5개의 태그 목록
    public ResponseEntity<List<TagResponse>> getRecommendedTags(@RequestHeader("Authorization") String token) {
        Long memberId = JwtUtil.extractAccessToken(token);
        List<TagResponse> recommendedTags = tagService.getRecommendedTags(memberId);
        return ResponseEntity.ok(recommendedTags);
    }

    @Operation(summary = "[마이페이지]내가 북마크한 태그 목록(최신순)")
    @GetMapping("/bookmarkedTags") // 내가 북마크한 태그 목록(최신순)
    public ResponseEntity<List<TagResponse>> getBookmarkedTags(@RequestHeader("Authorization") String token) {
        Long memberId = JwtUtil.extractAccessToken(token);
        List<TagResponse> bookmarkedTags = tagService.getBookmarkedTags(memberId);
        return ResponseEntity.ok(bookmarkedTags);
    }

    @Operation(summary = "[마이페이지]내가 좋아요 표시한 포스트 목록(최신순)")
    @GetMapping("/likedPosts") // 내가 좋아요 표시한 포스트 목록(최신순)
    public ResponseEntity<List<PostSimpleResponse>> getLikedPosts(@RequestHeader("Authorization") String token) {
        Long memberId = JwtUtil.extractAccessToken(token);
        List<PostSimpleResponse> likedPosts = postService.getLikedPosts(memberId);
        return ResponseEntity.ok(likedPosts);
    }

    @Operation(summary = "[마이페이지]내가 작성한 포스트 목록(최신순)")
    @GetMapping("/myPosts") // 내가 작성한 포스트 목록(최신순)
    public ResponseEntity<List<PostSimpleResponse>> getMyPosts(@RequestHeader("Authorization") String token) {
        Long memberId = JwtUtil.extractAccessToken(token);
        List<PostSimpleResponse> myPosts = postService.getMyPosts(memberId);
        return ResponseEntity.ok(myPosts);
    }

    @Operation(summary = "[마이페이지]내가 작성한 댓글 목록(최신순)")
    @GetMapping("/myComments") // 내가 작성한 댓글 목록(최신순)
    public ResponseEntity<List<CommentResponse>> getMyComments(@RequestHeader("Authorization") String token) {
        Long memberId = JwtUtil.extractAccessToken(token);
        List<CommentResponse> myComments = commentService.getMyComments(memberId);
        return ResponseEntity.ok(myComments);
    }
}
