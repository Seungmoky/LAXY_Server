package laxy.server.repository;

import laxy.server.entity.Member;
import laxy.server.entity.Tag;
import laxy.server.entity.TagBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagBookmarkRepository extends JpaRepository<TagBookmark, Long> {
    boolean existsByMemberAndTag(Member member, Tag tag);
    Optional<TagBookmark> findByMemberAndTag(Member member, Tag tag);
    List<TagBookmark> findByMember(Member member);
    List<TagBookmark> findByMemberOrderByCreatedAtDesc(Member member);
    List<TagBookmark> findTop5ByMemberOrderByCreatedAtDesc(Member member);
}
