package laxy.server.service;

import laxy.server.dto.request.CreateCommentRequest;
import laxy.server.dto.response.CommentResponse;
import laxy.server.entity.Comment;
import laxy.server.entity.CommentLike;
import laxy.server.entity.Member;
import laxy.server.entity.Post;
import laxy.server.exception.ApiException;
import laxy.server.exception.ErrorType;
import laxy.server.repository.CommentLikeRepository;
import laxy.server.repository.CommentRepository;
import laxy.server.repository.MemberRepository;
import laxy.server.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    // 인기순으로 댓글 조회
    public List<CommentResponse> getCommentsByPopularity(Long postId, Long memberId) {
        return getCommentsWithAdditionalInfo(postId, memberId, "popular");
    }

    // 최신순으로 댓글 조회
    public List<CommentResponse> getCommentsByRecent(Long postId, Long memberId) {
        return getCommentsWithAdditionalInfo(postId, memberId, "recent");
    }

    // 내가 작성한 댓글만 조회(memberId가 null인 경우 빈 리스트 반환)
    public List<CommentResponse> getCommentsByMy(Long postId, Long memberId) {
        if (memberId == null) return List.of();
        return getCommentsWithAdditionalInfo(postId, memberId, "my");
    }

    private List<CommentResponse> getCommentsWithAdditionalInfo(Long postId, Long memberId, String sortBy) {
        List<Comment> comments;

        // 기본적으로 최신순 조회 설정
        switch (sortBy.toLowerCase()) {
            case "popular":
                comments = commentRepository.findCommentsByPopularity(postId);
                break;
            case "my":
                comments = (memberId != null) ? commentRepository.findMyComments(memberId, postId) : List.of();
                break;
            case "recent":
            default:
                comments = commentRepository.findCommentsByRecent(postId);
                break;
        }

        Post post = postRepository.findById(postId).orElseThrow(() -> new ApiException(ErrorType.POST_NOT_FOUND));

        return comments.stream()
                .map(comment -> {
                    boolean isPoster = comment.getMember().getId().equals(post.getMember().getId());
                    boolean isMyComment = (memberId != null) && comment.getMember().getId().equals(memberId);
                    boolean isLiked = (memberId != null) && commentLikeRepository.existsByMemberAndComment(
                            memberRepository.findById(memberId).orElseThrow(() -> new ApiException(ErrorType.MEMBER_NOT_FOUND)),
                            comment
                    );
                    boolean isMyPost = (memberId != null) && post.getMember().getId().equals(memberId);
                    return new CommentResponse(comment, isPoster, isMyComment, isLiked, isMyPost);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void createComment(Long memberId, Long postId, CreateCommentRequest request) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(ErrorType.MEMBER_NOT_FOUND));
        Post post = postRepository.findById(postId).orElseThrow(() -> new ApiException(ErrorType.POST_NOT_FOUND));

        post.setComments(post.getComments() + 1);
        postRepository.save(post);

        Comment comment = new Comment(request.getContents(), member, post);
        commentRepository.save(comment);
    }

    @Transactional
    public void updateComment(Long memberId, Long commentId, CreateCommentRequest request) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ApiException(ErrorType.COMMENT_NOT_FOUND));

        if (!comment.getMember().getId().equals(memberId)) {
            throw new ApiException(ErrorType.INVALID_AUTHOR);
        }

        comment.setContents(request.getContents());
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long memberId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ApiException(ErrorType.COMMENT_NOT_FOUND));
        Post post = comment.getPost();

        // 댓글 작성자나 게시글 작성자 중 하나라도 일치하는지 확인
        if (!comment.getMember().getId().equals(memberId) && !post.getMember().getId().equals(memberId)) {
            throw new ApiException(ErrorType.INVALID_AUTHOR);
        }

        post.setComments(post.getComments() - 1);
        postRepository.save(post);

        commentRepository.delete(comment);
    }

    @Transactional
    public void likeComment(Long memberId, Long commentId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(ErrorType.MEMBER_NOT_FOUND));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ApiException(ErrorType.COMMENT_NOT_FOUND));

        if (commentLikeRepository.existsByMemberAndComment(member, comment)) {
            throw new ApiException(ErrorType.ALREADY_LIKED);
        }

        comment.setLikes(comment.getLikes() + 1);
        commentRepository.save(comment);

        CommentLike commentLike = new CommentLike(comment, member);
        commentLikeRepository.save(commentLike);
    }

    @Transactional
    public void unlikeComment(Long memberId, Long commentId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(ErrorType.MEMBER_NOT_FOUND));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ApiException(ErrorType.COMMENT_NOT_FOUND));
        CommentLike commentLike = commentLikeRepository.findByMemberAndComment(member, comment).orElseThrow(() -> new ApiException(ErrorType.NOT_LIKED));

        comment.setLikes(comment.getLikes() - 1);
        commentRepository.save(comment);

        commentLikeRepository.delete(commentLike);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getMyComments(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(ErrorType.MEMBER_NOT_FOUND));
        List<Comment> myComments = commentRepository.findByMemberOrderByCreatedAtDesc(member);

        return myComments.stream().map(comment -> {
            Post post = comment.getPost();
            boolean isPoster = post.getMember().getId().equals(memberId);
            boolean isMyComment = comment.getMember().getId().equals(memberId);
            boolean isLiked = commentLikeRepository.existsByMemberAndComment(member, comment);
            boolean isMyPost = post.getMember().getId().equals(memberId);
            return new CommentResponse(comment, isPoster, isMyComment, isLiked, isMyPost);
        }).collect(Collectors.toList());
    }
}