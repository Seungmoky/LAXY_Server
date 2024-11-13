package laxy.server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "게시글 생성 요청 정보")
public class CreatePostRequest {
    @NotBlank
    @Schema(description = "제목")
    private String title;

    @NotBlank
    @Schema(description = "내용")
    private String content;

    @Schema(description = "태그 목록")
    private List<String> tags;
}
