package laxy.server.service;

import laxy.server.dto.response.OrbitResponse;
import laxy.server.dto.response.TagResponse;
import laxy.server.entity.Association;
import laxy.server.entity.Member;
import laxy.server.entity.Tag;
import laxy.server.entity.TagBookmark;
import laxy.server.exception.ApiException;
import laxy.server.exception.ErrorType;
import laxy.server.repository.AssociationRepository;
import laxy.server.repository.MemberRepository;
import laxy.server.repository.TagBookmarkRepository;
import laxy.server.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final TagBookmarkRepository tagBookmarkRepository;
    private final MemberRepository memberRepository;
    private final AssociationRepository associationRepository;

    @Transactional(readOnly = true)
    public List<TagResponse> getTags() {
        List<Tag> tags = tagRepository.findAllByOrderByCreatedAtDesc();
        return tags.stream().map(TagResponse::new).collect(Collectors.toList());
    }

    // 특정 태그와 연관된 태그를 가중치 내림차순으로 조회
    @Transactional(readOnly = true)
    public List<TagResponse> getRelatedTagsByWeight(Long tagId) {
        Tag originalTag = tagRepository.findById(tagId).orElseThrow(() -> new ApiException(ErrorType.TAG_NOT_FOUND));

        List<Association> associations = associationRepository.findByOriginalTagIdOrderByWeightDesc(tagId);

        return associations.stream()
                .map(association -> new TagResponse(association.getRelatedTag()))
                .collect(Collectors.toList());
    }

    // 모든 태그를 게시물 수 기준으로 내림차순 정렬하여 조회
    @Transactional(readOnly = true)
    public List<TagResponse> getPopularCommunityTags() {
        List<Tag> communityTags = tagRepository.findAllByIsCommunityTrueOrderByPostsDesc();

        return communityTags.stream().map(TagResponse::new).collect(Collectors.toList());
    }

    @Transactional
    public void bookmarkTag(Long memberId, Long tagId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(ErrorType.MEMBER_NOT_FOUND));
        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new ApiException(ErrorType.TAG_NOT_FOUND));

        if (tagBookmarkRepository.existsByMemberAndTag(member, tag)) {
            throw new ApiException(ErrorType.ALREADY_BOOKMARKED);
        }

        tag.setBookmarks(tag.getBookmarks() + 1);
        tagRepository.save(tag);

        TagBookmark tagBookmark = new TagBookmark(member, tag);
        tagBookmarkRepository.save(tagBookmark);
    }

    @Transactional
    public void unBookmarkTag(Long memberId, Long tagId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(ErrorType.MEMBER_NOT_FOUND));
        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new ApiException(ErrorType.TAG_NOT_FOUND));

        tag.setBookmarks(tag.getBookmarks() - 1);
        tagRepository.save(tag);

        TagBookmark tagBookmark = tagBookmarkRepository.findByMemberAndTag(member, tag).orElseThrow(() -> new ApiException(ErrorType.NOT_BOOKMARKED));

        tagBookmarkRepository.delete(tagBookmark);
    }

    // 로그인 전 랜덤으로 5개의 커뮤니티 태그 가져오기
    @Transactional(readOnly = true)
    public List<TagResponse> getRandomCommunityTags() {
        List<Tag> communityTags = tagRepository.findAllByIsCommunityTrue();
        Random random = new Random();

        // 무작위로 4개의 태그를 선택 (총 태그 수가 5개 미만일 경우 그 수만큼 반환)
        return communityTags.stream()
                .sorted((t1, t2) -> random.nextInt(5) - 1) // 임의의 순서로 정렬
                .limit(5)
                .map(TagResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TagResponse> getMyRecentTags(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(ErrorType.MEMBER_NOT_FOUND));

        // 최근 북마크한 최대 5개의 태그 목록을 가져옵니다.
        List<TagBookmark> recentBookmarks = tagBookmarkRepository.findTop5ByMemberOrderByCreatedAtDesc(member);
        return recentBookmarks.stream().map(tagBookmark -> new TagResponse(tagBookmark.getTag())).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TagResponse> getRecommendedTags(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(ErrorType.MEMBER_NOT_FOUND));

        // 북마크한 태그들을 가져와서 연관도 높은 태그들을 계산하고 최대 5개 반환
        List<Tag> bookmarkedTags = tagBookmarkRepository.findByMember(member).stream().map(TagBookmark::getTag).collect(Collectors.toList());

        // 연관 태그를 찾고, 연관성 높은 순으로 정렬하여 최대 5개 반환 (구체적 연관성 로직은 필요에 따라 작성)
        List<Tag> relatedTags = tagRepository.findRelatedTags();
        return relatedTags.stream().limit(5).map(TagResponse::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TagResponse> getBookmarkedTags(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorType.MEMBER_NOT_FOUND));

        List<Tag> tags = tagBookmarkRepository.findByMemberOrderByCreatedAtDesc(member)
                .stream()
                .map(TagBookmark::getTag)
                .collect(Collectors.toList());
        return tags.stream().map(TagResponse::new).collect(Collectors.toList());
    }

    public List<TagResponse> getRelatedTags(Long tagId) {
        // 주어진 태그 ID에 대한 연관된 태그를 가중치 순으로 가져오고 4개까지만 제한
        List<Association> associations = associationRepository.findTop3ByOriginalTagIdOrderByWeightDesc(tagId);

        return associations.stream()
                .map(association -> new TagResponse(association.getRelatedTag()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void increaseAssociationWeight(Long originalTagId, Long relatedTagId) {
        Tag originalTag = tagRepository.findById(originalTagId).orElseThrow(() -> new ApiException(ErrorType.TAG_NOT_FOUND));
        Tag relatedTag = tagRepository.findById(relatedTagId).orElseThrow(() -> new ApiException(ErrorType.TAG_NOT_FOUND));

        Association association = associationRepository.findByOriginalTagAndRelatedTag(originalTag, relatedTag)
                .orElseGet(() -> new Association(originalTag, relatedTag, 5));

        association.setWeight(association.getWeight() + 5);
        associationRepository.save(association);
    }

    @Transactional(readOnly = true)
    public List<OrbitResponse> getTagOrbits(Long memberId) {
        List<Tag> baseTags;

        // `Authorization`이 있을 경우 북마크된 태그를 기준으로
        if (memberId != null) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new ApiException(ErrorType.MEMBER_NOT_FOUND));
            baseTags = tagBookmarkRepository.findTop5ByMemberOrderByCreatedAtDesc(member)
                    .stream()
                    .map(TagBookmark::getTag)
                    .collect(Collectors.toList());
        } else {
            // `Authorization`이 없을 경우 가중치가 높은 연관 태그를 기준으로
            baseTags = associationRepository.findTop5TagsByHighestWeight();
        }

        // 연관 태그 목록으로 `OrbitResponse` 리스트 생성
        return baseTags.stream().map(this::createOrbitResponse).collect(Collectors.toList());
    }

    // 단일 태그로 `OrbitResponse` 생성
    private OrbitResponse createOrbitResponse(Tag tag) {
        OrbitResponse response = new OrbitResponse();
        response.setCenter(new OrbitResponse.TagInfo(tag.getId(), tag.getName(), tag.getGrade()));

        // 연관 태그 조회, 가중치 순 정렬 및 상위 5개 제한
        List<OrbitResponse.TagInfo> satellites = associationRepository.findByOriginalTagIdOrderByWeightDesc(tag.getId())
                .stream()
                .limit(5)
                .map(association -> {
                    Tag relatedTag = association.getRelatedTag();
                    return new OrbitResponse.TagInfo(relatedTag.getId(), relatedTag.getName(), relatedTag.getGrade());
                })
                .collect(Collectors.toList());

        response.setSatellites(satellites);
        return response;
    }

    @Transactional(readOnly = true)
    public boolean isTagBookmarked(Long memberId, Long tagId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorType.MEMBER_NOT_FOUND));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ApiException(ErrorType.TAG_NOT_FOUND));

        // 북마크 여부 확인
        return tagBookmarkRepository.existsByMemberAndTag(member, tag);
    }

    @Transactional(readOnly = true)
    public List<TagResponse> searchTagsByName(String query) {
        List<Tag> tags = tagRepository.findByNameContaining(query);
        return tags.stream().map(TagResponse::new).collect(Collectors.toList());
    }
}
