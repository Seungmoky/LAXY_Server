package laxy.server.repository;

import laxy.server.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    // 최신순으로 모든 태그 조회
    List<Tag> findAllByOrderByCreatedAtDesc();

    // 모든 태그를 게시물 수 기준으로 내림차순 정렬하여 조회
    @Query("SELECT t FROM Tag t ORDER BY size(t.posts) DESC")
    List<Tag> findAllByIsCommunityTrueOrderByPostsDesc();

    // 커뮤니티로 승격된 태그 목록 가져오기
    @Query("SELECT t FROM Tag t WHERE t.isCommunity = true")
    List<Tag> findAllByIsCommunityTrue();

    // 주어진 태그들과 연관성이 높은 태그들을 찾기 위한 쿼리 (커뮤니티만 대상으로 함) // 일단 랜덤으로 뽑아오도록 변경
    @Query(value = "SELECT * FROM tag t WHERE t.is_community = true ORDER BY RAND() LIMIT 10", nativeQuery = true)
    List<Tag> findRelatedTags();

    List<Tag> findByNameContaining(String query);
}
