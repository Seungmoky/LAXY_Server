package laxy.server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "댓글 생성 요청 정보")
public class CreateCommentRequest {

    @NotBlank(message = "댓글 내용은 필수 입력 값입니다.")
    @Schema(description = "내용")
    private String contents;
}