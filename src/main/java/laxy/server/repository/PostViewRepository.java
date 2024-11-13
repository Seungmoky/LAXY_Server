package laxy.server.repository;

import laxy.server.entity.Member;
import laxy.server.entity.Post;
import laxy.server.entity.PostView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostViewRepository extends JpaRepository<PostView, Long> {
    boolean existsByMemberAndPost(Member member, Post post);
}
