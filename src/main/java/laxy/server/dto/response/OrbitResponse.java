package laxy.server.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "궤도 정보")
public class OrbitResponse {

    @Schema(description = "기준 태그 정보")
    private TagInfo center;

    @Schema(description = "연관된 태그 목록")
    private List<TagInfo> satellites;

    @Data
    @Schema(description = "태그 정보")
    public static class TagInfo {
        @Schema(description = "태그 아이디")
        private Long tagId;

        @Schema(description = "태그 이름")
        private String name;

        @Schema(description = "태그 등급")
        private int grade;

        public TagInfo(Long tagId, String name, int grade) {
            this.tagId = tagId;
            this.name = name;
            this.grade = grade;
        }
    }
}
