package laxy.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import laxy.server.dto.request.IncreaseTagAssociationRequest;
import laxy.server.dto.response.OrbitResponse;
import laxy.server.dto.response.TagResponse;
import laxy.server.service.TagService;
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
@Tag(name = "태그 및 커뮤니티 관련 API")
public class TagController {

    private final TagService tagService;

    @Operation(summary = "[메인]태그 목록 조회(최신순)")
    @GetMapping("/tag") // 태그 조회
    public ResponseEntity<List<TagResponse>> getTags() {
        List<TagResponse> tags = tagService.getTags();
        return ResponseEntity.ok(tags);
    }

    @Operation(summary = "[커뮤니티]추천 태그(해당 태그와 관련된 태그 순위, 가중치 내림차순)")
    @GetMapping("/tag/related/{tag_id}")
    public ResponseEntity<List<TagResponse>> getRelatedTagsByWeight(@PathVariable("tag_id") Long tagId) {
        List<TagResponse> relatedTags = tagService.getRelatedTagsByWeight(tagId);
        return ResponseEntity.ok(relatedTags);
    }

    @Operation(summary = "[트렌드]인기 커뮤니티(관련된 게시글 수 기준 내림차순, 태그도 조회)")
    @GetMapping("/tag/popular") // 인기 커뮤니티 조회
    public ResponseEntity<List<TagResponse>> getPopularCommunityTags() {
        List<TagResponse> popularTags = tagService.getPopularCommunityTags();
        return ResponseEntity.ok(popularTags);
    }

    @Operation(summary = "[태그/커뮤니티]태그 북마크 등록")
    @PostMapping("/tag/bookmark/{tag_id}") // 태그 북마크 등록
    public ResponseEntity<String> bookmarkTag(@RequestHeader("Authorization") String token, @PathVariable Long tag_id) {
        Long memberId = JwtUtil.extractAccessToken(token);
        tagService.bookmarkTag(memberId, tag_id);
        return ResponseEntity.ok("success");
    }

    @Operation(summary = "[태그/커뮤니티]태그 북마크 해제")
    @DeleteMapping("/tag/bookmark/{tag_id}") // 태그 북마크 해제
    public ResponseEntity<String> unBookmarkTag(@RequestHeader("Authorization") String token, @PathVariable Long tag_id) {
        Long memberId = JwtUtil.extractAccessToken(token);
        tagService.unBookmarkTag(memberId, tag_id);
        return ResponseEntity.ok("success");
    }

//    @Operation(summary = "[마인드맵]연관된 태그 추천 최대 3개 조회")
//    @GetMapping("/tag/recommend/{tag_id}") // 연관된 태그 추천 조회
//    public ResponseEntity<List<TagResponse>> getRelatedTags(@RequestHeader("Authorization") String token, @PathVariable Long tag_id) {
//        Long memberId = JwtUtil.extractAccessToken(token);
//        List<TagResponse> relatedTags = tagService.getRelatedTags(tag_id);
//        return ResponseEntity.ok(relatedTags);
//    }

    @Operation(summary = "[태그]연관 태그 가중치 증가")
    @PostMapping("/tag/association")
    public ResponseEntity<String> increaseAssociationWeight(
            @RequestBody @Valid IncreaseTagAssociationRequest request) {
        tagService.increaseAssociationWeight(request.getOriginalTagId(), request.getRelatedTagId());
        return ResponseEntity.ok("success");
    }

    @Operation(summary = "[마인드맵]궤도 정보 조회")
    @GetMapping("/tag/orbits")
    public ResponseEntity<List<OrbitResponse>> getTagOrbits(
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long memberId = (token != null) ? JwtUtil.extractAccessToken(token) : null;
        List<OrbitResponse> orbits = tagService.getTagOrbits(memberId);
        return ResponseEntity.ok(orbits);
    }

    @Operation(summary = "[태그/커뮤니티]해당 태그 북마크 여부 확인")
    @GetMapping("/tag/bookmark/{tag_id}") // 태그 북마크 여부 확인
    public ResponseEntity<Boolean> isTagBookmarked(
            @RequestHeader("Authorization") String token,
            @PathVariable Long tag_id) {
        Long memberId = JwtUtil.extractAccessToken(token);
        boolean isBookmarked = tagService.isTagBookmarked(memberId, tag_id);
        return ResponseEntity.ok(isBookmarked);
    }

    @Operation(summary = "[태그/커뮤니티]태그 검색")
    @GetMapping("/tag/search")
    public ResponseEntity<List<TagResponse>> searchTags(
            @RequestParam("query") String query) {
        List<TagResponse> tags = tagService.searchTagsByName(query);
        return ResponseEntity.ok(tags);
    }
}
