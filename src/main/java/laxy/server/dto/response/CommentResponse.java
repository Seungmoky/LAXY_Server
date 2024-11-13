package laxy.server.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import laxy.server.entity.Comment;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
@Schema(description = "댓글 정보")
public class CommentResponse {

    @Schema(description = "댓글 아이디")
    private Long commentId;

    @Schema(description = "댓글 내용")
    private String contents;

    @Schema(description = "댓글 작성자")
    private String author;

    @Schema(description = "댓글 생성 일시 yyyy-MM-dd HH:mm:ss 형식")
    private String createdAt; // 생성 일시 yyyy-MM-dd HH:mm:ss

    @Schema(description = "댓글 좋아요 수")
    private int likeCount; // 좋아요 수

    @Schema(description = "연관된 게시물 아이디")
    private Long postId; // 연관된 게시물의 아이디 추가

    @JsonProperty("isPoster")
    @Schema(description = "게시글 작성자의 댓글인지 여부")
    private boolean isPoster; // 게시글 작성자 여부

    @JsonProperty("isMyComment")
    @Schema(description = "내가 작성한 댓글인지 여부")
    private boolean isMyComment; // 내가 작성한 댓글인지 여부

    @JsonProperty("isLiked")
    @Schema(description = "내가 댓글에 좋아요를 눌렀는지 여부")
    private boolean isLiked; // 내가 좋아요를 눌렀는지 여부

    @JsonProperty("isMyPost")
    @Schema(description = "내가 작성한 게시글의 댓글인지 여부")
    private boolean isMyPost; // 내가 작성한 게시글의 댓글 여부

//    public CommentResponse(Comment comment, boolean isPoster, boolean isMyComment, boolean isLiked) {
//        this.commentId = comment.getId();
//        this.contents = comment.getContents();
//        this.author = comment.getMember().getName();
//        this.createdAt = comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        this.likeCount = comment.getLikes();
//        this.postId = comment.getPost().getId();
//        this.isPoster = isPoster;
//        this.isMyComment = isMyComment;
//        this.isLiked = isLiked;
//    }

    public CommentResponse(Comment comment, boolean isPoster, boolean isMyComment, boolean isLiked, boolean isMyPost) {
        this.commentId = comment.getId();
        this.contents = comment.getContents();
        this.author = comment.getMember().getName();
        this.createdAt = comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.likeCount = comment.getLikes();
        this.postId = comment.getPost().getId();
        this.isPoster = isPoster;
        this.isMyComment = isMyComment;
        this.isLiked = isLiked;
        this.isMyPost = isMyPost;
    }
}