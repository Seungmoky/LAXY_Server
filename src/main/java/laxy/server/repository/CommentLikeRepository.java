package laxy.server.repository;

import laxy.server.entity.Comment;
import laxy.server.entity.CommentLike;
import laxy.server.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByMemberAndComment(Member member, Comment comment);
    Optional<CommentLike> findByMemberAndComment(Member member, Comment comment);
}
