package laxy.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tag_bookmark")
public class TagBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 태그 북마크 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag; // 북마크된 태그

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member; // 태그를 북마크한 회원

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일시 yyyy-MM-dd HH:mm:ss

    public TagBookmark(Member member, Tag tag) {
        this.member = member;
        this.tag = tag;
        this.createdAt = LocalDateTime.now();
    }
}
