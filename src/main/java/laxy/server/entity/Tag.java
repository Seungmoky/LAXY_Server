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
@Table(name = "tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 태그 ID

    @Column(nullable = false)
    private String name; // 태그 이름

    @ManyToMany(mappedBy = "tags")
    private List<Post> posts; // 해당 태그를 사용하는 게시물 리스트

    private boolean isCommunity; // 커뮤니티 여부(관련 포스트가 5개 이상이면 true, 아니면 false)

    private int bookmarks; // 북마크 수

    private int grade; // 등급 값

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일시 yyyy-MM-dd HH:mm:ss

    @Column(nullable = false)
    private LocalDateTime updatedAt; // 수정 일시 yyyy-MM-dd HH:mm:ss

    public Tag(String name) {
        this.name = name;
        this.isCommunity = false;
        this.bookmarks = 0;
        this.grade = 1;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 게시물 수에 따라 등급을 업데이트하는 메서드
    public void updateGrade(int postCount) {
        if (postCount <= 2) {
            this.grade = 1;
        } else if (postCount <= 4) {
            this.grade = 2;
        } else if (postCount <= 6) {
            this.grade = 3;
        } else if (postCount <= 8) {
            this.grade = 4;
        } else if (postCount <= 10) {
            this.grade = 5;
        } else if (postCount <= 100) {
            this.grade = 6;
        } else if (postCount <= 1000) {
            this.grade = 7;
        } else if (postCount <= 10000) {
            this.grade = 8;
        } else if (postCount <= 100000) {
            this.grade = 9;
        } else {
            this.grade = 10;
        }
    }
}
