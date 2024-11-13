package laxy.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post_like")
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 좋아요 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // 게시물

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member; // 좋아요를 누른 회원

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 좋아요 일시 yyyy-MM-dd HH:mm:ss

    public PostLike(Post post, Member member) {
        this.post = post;
        this.member = member;
        this.createdAt = LocalDateTime.now();
    }
}
