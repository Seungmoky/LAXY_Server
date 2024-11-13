package laxy.server.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import laxy.server.entity.Post;
import laxy.server.entity.Tag;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Schema(description = "게시글 상세 정보")
public class PostDetailResponse {

    @Schema(description = "게시글 아이디")
    private Long postId;

    @Schema(description = "게시글 제목")
    private String title;

    @Schema(description = "게시글 내용")
    private String contents;

    @Schema(description = "게시글 썸네일 이미지 URL")
    private String imageUrl; // 썸네일 이미지 URL 추가

    @Schema(description = "게시글 작성자")
    private String author;

    @Schema(description = "게시글 생성 일시 yyyy-MM-dd HH:mm:ss 형식")
    private String createdAt; // 생성 일시 yyyy-MM-dd HH:mm:ss

    @Schema(description = "게시글 태그 목록")
    private List<TagInfo> tags; // 태그 정보 리스트

    @Schema(description = "게시글 댓글 수")
    private int commentCount;

    @Schema(description = "게시글 좋아요 수")
    private int likeCount;

    @Schema(description = "게시글 조회수")
    private int viewCount;

    @JsonProperty("isMyPost")
    @Schema(description = "내가 작성한 게시물인지 여부")
    private boolean isMyPost; // 내가 작성한 게시물인지 여부

    @JsonProperty("isLiked")
    @Schema(description = "내가 게시글에 좋아요를 눌렀는지 여부")
    private boolean isLiked; // 내가 좋아요를 눌렀는지 여부

    public PostDetailResponse(Post post, boolean isMyPost, boolean isLiked) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.imageUrl = post.getImageUrl();
        this.author = post.getMember().getName();
        this.createdAt = post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.tags = post.getTags().stream().map(TagInfo::new).collect(Collectors.toList());
        this.commentCount = post.getComments();
        this.likeCount = post.getLikes();
        this.viewCount = post.getViews();
        this.isMyPost = isMyPost;
        this.isLiked = isLiked;
    }

    @Data
    public static class TagInfo {
        @Schema(description = "태그 아이디")
        private Long id;

        @Schema(description = "태그 이름")
        private String name;

        @Schema(description = "태그 등급")
        private int grade;

        public TagInfo(Tag tag) {
            this.id = tag.getId();
            this.name = tag.getName();
            this.grade = tag.getGrade();
        }
    }
}
