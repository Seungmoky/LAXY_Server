package laxy.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment_like")
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 댓글 좋아요 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment; // 좋아요한 댓글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member; // 좋아요를 누른 회원

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 좋아요 일시 yyyy-MM-dd HH:mm:ss

    public CommentLike(Comment comment, Member member) {
        this.comment = comment;
        this.member = member;
        this.createdAt = LocalDateTime.now();
    }
}
