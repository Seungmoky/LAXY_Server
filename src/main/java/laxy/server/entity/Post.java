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
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 게시물 ID

    @Column(nullable = false)
    private String title; // 게시물 제목

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contents; // 게시물 내용(JSON 형태)

    @Column(name = "image_url")
    private String imageUrl; // 썸네일 이미지 URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member; // 작성자

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags; // 태그 리스트

    private int views = 0; // 조회수

    private int likes = 0; // 좋아요 수

    private int comments = 0; // 댓글 수

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일시 yyyy-MM-dd HH:mm:ss

    @Column(nullable = false)
    private LocalDateTime updatedAt; // 수정 일시 yyyy-MM-dd HH:mm:ss

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PostLike> postLikes; // 게시물 좋아요 목록

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PostView> postViews; // 게시물 조회 목록

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> commentsList; // 게시물 댓글 목록

    public Post(String title, String contents, String imageUrl, Member member, List<Tag> tags) {
        this.title = title;
        this.contents = contents;
        this.imageUrl = imageUrl;
        this.member = member;
        this.tags = tags;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
