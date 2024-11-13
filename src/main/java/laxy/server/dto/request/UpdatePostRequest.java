package laxy.server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "게시글 수정 요청 정보")
public class UpdatePostRequest {

    @Schema(description = "제목")
    private String title;

    @Schema(description = "내용")
    private String contents;

    @Schema(description = "태그 이름 목록")
    private List<String> tags;
}
