package laxy.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 댓글 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // 게시물

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member; // 댓글 작성자

    @Column(nullable = false)
    private String contents; // 댓글 내용

    private int likes = 0; // 좋아요 수

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일시 yyyy-MM-dd HH:mm:ss

    @Column(nullable = false)
    private LocalDateTime updatedAt; // 수정 일시 yyyy-MM-dd HH:mm:ss

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CommentLike> commentLikes; // 댓글 좋아요 목록

    public Comment(String contents, Member member, Post post) {
        this.contents = contents;
        this.member = member;
        this.post = post;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
