package laxy.server.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import laxy.server.dto.request.CreatePostRequest;
import laxy.server.dto.request.UpdatePostRequest;
import laxy.server.dto.response.PostDetailResponse;
import laxy.server.dto.response.PostSimpleResponse;
import laxy.server.entity.*;
import laxy.server.exception.ApiException;
import laxy.server.exception.ErrorType;
import laxy.server.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostViewRepository postViewRepository;
    private final PostLikeRepository postLikeRepository;
    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final AssociationRepository associationRepository;

    private static final int COMMUNITY_PROMOTION_THRESHOLD = 10; // 커뮤니티 승격 기준
    private static final int LIKE_THRESHOLD = 5; // 좋아요 수 기준 상수 추가

    @Transactional
    public Long createPost(Long memberId, CreatePostRequest request) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(ErrorType.MEMBER_NOT_FOUND));

        // 태그 개수 유효성 검사
        if (request.getTags() == null || request.getTags().isEmpty()) {
            new ApiException(ErrorType.TAG_COUNT_TOO_LOW);
        }
        if (request.getTags().size() > 10) {
            new ApiException(ErrorType.TAG_COUNT_TOO_HIGH);
        }

        // 태그 이름을 기반으로 Tag 엔티티 목록 생성
        List<Tag> tags = request.getTags().stream().map(this::getOrCreateTag).collect(Collectors.toList());

        // 첫 번째 이미지 URL을 추출하여 썸네일로 설정
        String thumbnailUrl = extractThumbnailUrl(request.getContent());

        // Post 엔티티 생성 및 저장
        Post post = new Post(request.getTitle(), request.getContent(), thumbnailUrl, member, tags);
        postRepository.save(post);

        // 태그별 커뮤니티 승격 여부 업데이트
        for (Tag tag : tags) {
            int postCount = postRepository.countByTags(tag);

            if (postRepository.countByTags(tag) >= COMMUNITY_PROMOTION_THRESHOLD) {
                tag.setCommunity(true);
                tagRepository.save(tag);
            } else {
                tag.setCommunity(false);
                tagRepository.save(tag);
            }

            // 게시물 수에 따라 등급 업데이트
            tag.updateGrade(postCount);

            tagRepository.save(tag);
        }

        // 연관도 업데이트
        for (Tag originalTag : tags) {
            for (Tag relatedTag : tags) {
                if (!originalTag.equals(relatedTag)) {
                    updateTagAssociation(originalTag, relatedTag);
                }
            }
        }

        return post.getId();
    }

    // JSON에서 첫 번째 이미지 URL을 추출하는 메서드
    public String extractThumbnailUrl(String contentJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(contentJson);

            for (JsonNode node : rootNode) { // 배열의 각 노드를 순회
                JsonNode imageNode = node.path("insert").path("image");
                if (!imageNode.isMissingNode()) { // image 필드가 존재할 경우
                    return imageNode.asText(); // 첫 번째 이미지 URL 반환
                }
            }
        } catch (Exception e) {
            log.info("썸네일 추출 중 예외 발생: {}", e.getMessage());
            e.printStackTrace();
        }
        return null; // 이미지 URL이 없을 경우 null 반환
    }

    public List<PostSimpleResponse> getDailyPopularPosts(String tagName) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        List<Post> posts = postRepository.findDailyPostsByTag(tagName, startDate);
        return posts.stream().map(PostSimpleResponse::new).collect(Collectors.toList());
    }

    public List<PostSimpleResponse> getWeeklyPopularPosts(String tagName) {
        LocalDateTime startDate = LocalDateTime.now().minusWeeks(1);
        List<Post> posts = postRepository.findWeeklyPostsByTag(tagName, startDate);
        return posts.stream().map(PostSimpleResponse::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostSimpleResponse> getLikedPosts(String tagName) {
        List<Post> posts = postRepository.findMostLikedPostsByTag(tagName, LIKE_THRESHOLD);
        return posts.stream().map(PostSimpleResponse::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostSimpleResponse> getAllPostsByTag(String tagName, String sortBy) {
        List<Post> posts;

        switch (sortBy.toLowerCase()) {
            case "likes":
                posts = postRepository.findAllPostsByTagSortedByLikes(tagName);
                break;
            case "views":
                posts = postRepository.findAllPostsByTagSortedByViews(tagName);
                break;
            case "recent":
            default:
                posts = postRepository.findAllPostsByTagSortedByRecent(tagName);
                break;
        }

        return posts.stream().map(PostSimpleResponse::new).collect(Collectors.toList());
    }

    @Transactional
    public void updatePost(Long memberId, Long postId, UpdatePostRequest request) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ApiException(ErrorType.POST_NOT_FOUND));

        if (!post.getMember().getId().equals(memberId)) {
            throw new ApiException(ErrorType.INVALID_AUTHOR);
        }

        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }

        if (request.getContents() != null) {
            post.setContents(request.getContents());
        }

        if (request.getTags() != null) {
            List<Tag> tags = request.getTags().stream().map(this::getOrCreateTag).collect(Collectors.toList());
            post.setTags(tags);
        }

        postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long memberId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ApiException(ErrorType.POST_NOT_FOUND));

        if (!post.getMember().getId().equals(memberId)) {
            throw new ApiException(ErrorType.INVALID_AUTHOR);
        }

        List<Tag> tags = post.getTags();

        postRepository.delete(post);

        for (Tag tag : tags) {
            int postCount = postRepository.countByTags(tag);

            // 커뮤니티 승격 여부 업데이트
            if (postCount >= COMMUNITY_PROMOTION_THRESHOLD) {
                tag.setCommunity(true);
            } else {
                tag.setCommunity(false);
            }

            // 게시물 수에 따른 등급 업데이트
            tag.updateGrade(postCount);

            tagRepository.save(tag);
        }
    }

    @Transactional
    public PostDetailResponse getPostDetail(Long memberId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ApiException(ErrorType.POST_NOT_FOUND));

        boolean isMyPost = (memberId != null && post.getMember().getId().equals(memberId));
        boolean isLiked = (memberId != null && postLikeRepository.existsByMemberIdAndPostId(memberId, postId));

        // 조회수 증가를 위한 PostView 처리 로직 추가
        if (memberId != null) {
            Member member = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(ErrorType.MEMBER_NOT_FOUND));

            if (!postViewRepository.existsByMemberAndPost(member, post)) {
                PostView postView = new PostView(post, member);
                postViewRepository.save(postView);
                post.setViews(post.getViews() + 1); // Post 엔티티의 조회수 증가 메서드
                postRepository.save(post); // 변경된 조회수 저장
            }
        }

        return new PostDetailResponse(post, isMyPost, isLiked);
    }

    @Transactional
    public void likePost(Long memberId, Long postId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(ErrorType.MEMBER_NOT_FOUND));
        Post post = postRepository.findById(postId).orElseThrow(() -> new ApiException(ErrorType.POST_NOT_FOUND));

        if (postLikeRepository.existsByMemberAndPost(member, post)) {
            throw new ApiException(ErrorType.ALREADY_LIKED);
        }

        post.setLikes(post.getLikes() + 1);
        postRepository.save(post);

        postLikeRepository.save(new PostLike(post, member));
    }

    @Transactional
    public void unlikePost(Long memberId, Long postId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(ErrorType.MEMBER_NOT_FOUND));
        Post post = postRepository.findById(postId).orElseThrow(() -> new ApiException(ErrorType.POST_NOT_FOUND));
        PostLike postLike = postLikeRepository.findByMemberAndPost(member, post).orElseThrow(() -> new ApiException(ErrorType.NOT_LIKED));

        post.setLikes(post.getLikes() - 1);
        postRepository.save(post);

        postLikeRepository.delete(postLike);
    }

    @Transactional(readOnly = true)
    public List<PostSimpleResponse> getLikedPosts(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorType.MEMBER_NOT_FOUND));

        List<Post> likedPosts = postLikeRepository.findByMemberOrderByCreatedAtDesc(member)
                .stream()
                .map(PostLike::getPost)
                .collect(Collectors.toList());
        return likedPosts.stream().map(PostSimpleResponse::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostSimpleResponse> getMyPosts(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorType.MEMBER_NOT_FOUND));

        List<Post> myPosts = postRepository.findByMemberOrderByCreatedAtDesc(member);
        return myPosts.stream().map(PostSimpleResponse::new).collect(Collectors.toList());
    }

    // 공통 함수

    private Tag getOrCreateTag(String tagName) {
        return tagRepository.findByName(tagName).orElseGet(() -> {
            Tag newTag = new Tag(tagName);
            return tagRepository.save(newTag);
        });
    }

    private void updateTagAssociation(Tag originalTag, Tag relatedTag) {
        Optional<Association> associationOptional = associationRepository.findByOriginalTagAndRelatedTag(originalTag, relatedTag);

        if (associationOptional.isPresent()) {
            // 이미 존재하는 연관도 -> 가중치 3 증가
            Association association = associationOptional.get();
            association.setWeight(association.getWeight() + 3);
            associationRepository.save(association);
        } else {
            // 새로운 연관도 생성
            Association newAssociation = new Association(originalTag, relatedTag, 3);
            associationRepository.save(newAssociation);
        }
    }

    public List<PostSimpleResponse> getDailyPopularPosts() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        List<Post> posts = postRepository.findDailyPosts(startDate);
        return posts.stream().map(PostSimpleResponse::new).collect(Collectors.toList());
    }

    public List<PostSimpleResponse> getWeeklyPopularPosts() {
        LocalDateTime startDate = LocalDateTime.now().minusWeeks(1);
        List<Post> posts = postRepository.findWeeklyPosts(startDate);
        return posts.stream().map(PostSimpleResponse::new).collect(Collectors.toList());
    }

    public List<PostSimpleResponse> getAllPosts(String sortBy) {
        List<Post> posts;

        switch (sortBy.toLowerCase()) {
            case "likes":
                posts = postRepository.findAllPostsSortedByLikes();
                break;
            case "views":
                posts = postRepository.findAllPostsSortedByViews();
                break;
            case "recent":
            default:
                posts = postRepository.findAllPostsSortedByRecent();
                break;
        }

        return posts.stream().map(PostSimpleResponse::new).collect(Collectors.toList());
    }
}
