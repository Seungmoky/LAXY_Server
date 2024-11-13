package laxy.server.repository;

import laxy.server.entity.Member;
import laxy.server.entity.Post;
import laxy.server.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByMemberAndPost(Member member, Post post);
    Optional<PostLike> findByMemberAndPost(Member member, Post post);
    boolean existsByMemberIdAndPostId(Long memberId, Long postId);
    List<PostLike> findByMemberOrderByCreatedAtDesc(Member member);
}
