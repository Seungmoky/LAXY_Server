package laxy.server.repository;

import laxy.server.entity.Member;
import laxy.server.entity.Post;
import laxy.server.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 1. 태크/커뮤니티 일간 인기 게시글 (조회수 기준 상위 10개)
    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.name = :tagName AND p.createdAt >= :startDate ORDER BY p.views DESC")
    List<Post> findDailyPostsByTag(@Param("tagName") String tagName, @Param("startDate") LocalDateTime startDate);

    // 2. 태크/커뮤니티 주간 인기 게시글 (조회수 기준 상위 10개)
    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.name = :tagName AND p.createdAt >= :startDate ORDER BY p.views DESC")
    List<Post> findWeeklyPostsByTag(@Param("tagName") String tagName, @Param("startDate") LocalDateTime startDate);

    // 3. 태크/커뮤니티 좋아요 수가 5개 이상인 게시글 (좋아요 기준 내림차순)
    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.name = :tagName AND p.likes >= :likeThreshold ORDER BY p.likes DESC")
    List<Post> findMostLikedPostsByTag(@Param("tagName") String tagName, @Param("likeThreshold") int likeThreshold);

    // 4. 태크/커뮤니티 게시글 좋아요순
    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.name = :tagName ORDER BY p.likes DESC")
    List<Post> findAllPostsByTagSortedByLikes(@Param("tagName") String tagName);

    // 4. 태크/커뮤니티 게시글 조회수순
    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.name = :tagName ORDER BY p.views DESC")
    List<Post> findAllPostsByTagSortedByViews(@Param("tagName") String tagName);

    // 4. 태크/커뮤니티 게시글 최신순
    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.name = :tagName ORDER BY p.createdAt DESC")
    List<Post> findAllPostsByTagSortedByRecent(@Param("tagName") String tagName);

    int countByTags(Tag tag); // 특정 태그와 연관된 게시글의 수를 반환하는 메서드

    List<Post> findByMemberOrderByCreatedAtDesc(Member member);

    // 1. 전체 일간 인기 게시글 (조회수 기준 상위 10개)
    @Query("SELECT p FROM Post p WHERE p.createdAt >= :startDate ORDER BY p.views DESC")
    List<Post> findDailyPosts(@Param("startDate") LocalDateTime startDate);

    // 2. 전체 주간 인기 게시글 (조회수 기준 상위 10개)
    @Query("SELECT p FROM Post p WHERE p.createdAt >= :startDate ORDER BY p.views DESC")
    List<Post> findWeeklyPosts(@Param("startDate") LocalDateTime startDate);

    // 4. 전체 게시글 좋아요순
    @Query("SELECT p FROM Post p ORDER BY p.likes DESC")
    List<Post> findAllPostsSortedByLikes();

    // 4. 전체 게시글 조회수순
    @Query("SELECT p FROM Post p ORDER BY p.views DESC")
    List<Post> findAllPostsSortedByViews();

    // 4. 전체 게시글 최신순
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findAllPostsSortedByRecent();
}
