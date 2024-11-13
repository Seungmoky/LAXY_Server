package laxy.server.repository;

import laxy.server.entity.Association;
import laxy.server.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AssociationRepository extends JpaRepository<Association, Long> {
    Optional<Association> findByOriginalTagAndRelatedTag(Tag originalTag, Tag relatedTag);
    List<Association> findTop3ByOriginalTagIdOrderByWeightDesc(Long originalTagId);

    // 가중치 내림차순으로 정렬된 연관 태그 모두 조회
    List<Association> findByOriginalTagIdOrderByWeightDesc(Long originalTagId);

    // 가중치가 가장 높은 연관 태그 최대 5개 조회
    @Query("SELECT a.relatedTag FROM Association a ORDER BY a.weight DESC")
    List<Tag> findTop5TagsByHighestWeight();
}
