package laxy.server.repository;

import laxy.server.entity.Comment;
import laxy.server.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 좋아요 수가 많은 순으로 정렬, 좋아요 수가 같으면 최신순 정렬
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.likes DESC, c.createdAt DESC")
    List<Comment> findCommentsByPopularity(@Param("postId") Long postId);

    // 최신순으로 정렬
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.createdAt DESC")
    List<Comment> findCommentsByRecent(@Param("postId") Long postId);

    // 내가 작성한 댓글만 조회
    @Query("SELECT c FROM Comment c WHERE c.member.id = :memberId AND c.post.id = :postId ORDER BY c.createdAt DESC")
    List<Comment> findMyComments(@Param("memberId") Long memberId, @Param("postId") Long postId);

    // 사용자가 작성한 댓글을 최신순으로 가져오기
    List<Comment> findByMemberOrderByCreatedAtDesc(Member member);
}
