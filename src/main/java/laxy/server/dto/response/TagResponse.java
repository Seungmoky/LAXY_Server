package laxy.server.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import laxy.server.entity.Tag;
import lombok.Data;

@Data
@Schema(description = "태그(or 커뮤니티) 정보")
public class TagResponse {

    @Schema(description = "태그 아이디")
    private Long tagId;

    @Schema(description = "태그 이름")
    private String name;

    @JsonProperty("isCommunity")
    @Schema(description = "커뮤니티로 승격했는지 여부")
    private boolean isCommunity;

    @Schema(description = "태그 북마크 수")
    private int bookmarkCount;

    @Schema(description = "관련된 게시물 수")
    private int postCount; // 관련된 게시물 수

    @Schema(description = "태그 등급")
    private int grade; // 태그의 등급 정보 추가

    public TagResponse(Tag tag) {
        this.tagId = tag.getId();
        this.name = tag.getName();
        this.isCommunity = tag.isCommunity();
        this.bookmarkCount = tag.getBookmarks();
        this.postCount = tag.getPosts() != null ? tag.getPosts().size() : 0;
        this.grade = tag.getGrade();
    }
}
