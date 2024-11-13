package laxy.server.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import laxy.server.entity.Post;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
@Schema(description = "게시글 목록 형태의 정보")
public class PostSimpleResponse {

    @Schema(description = "게시글 아이디")
    private Long postId;

    @Schema(description = "게시글 제목")
    private String title;

    @Schema(description = "게시글 작성자")
    private String author;

    @Schema(description = "게시글 생성 일시 yyyy-MM-dd HH:mm:ss 형식")
    private String createdAt; // 생성 일시 yyyy-MM-dd HH:mm:ss

    @Schema(description = "게시글 댓글 수")
    private int commentCount;

    @Schema(description = "게시글 좋아요 수")
    private int likeCount;

    @Schema(description = "게시글 조회수")
    private int viewCount;

    @Schema(description = "게시글 썸네일 이미지 URL")
    private String imageUrl; // 썸네일 이미지 URL 추가

    public PostSimpleResponse(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.author = post.getMember().getName(); // 작성자 이름
        this.createdAt = post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // yyyy-MM-dd HH:mm:ss 형식으로 변환
        this.commentCount = post.getComments();
        this.likeCount = post.getLikes();
        this.viewCount = post.getViews();
        this.imageUrl = post.getImageUrl();
    }
}
