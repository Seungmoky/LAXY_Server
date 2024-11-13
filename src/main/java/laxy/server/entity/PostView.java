package laxy.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post_view")
public class PostView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 조회 기록 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // 게시물

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member; // 조회한 회원

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 조회 일시 yyyy-MM-dd HH:mm:ss

    public PostView(Post post, Member member) {
        this.post = post;
        this.member = member;
        this.createdAt = LocalDateTime.now();
    }
}
