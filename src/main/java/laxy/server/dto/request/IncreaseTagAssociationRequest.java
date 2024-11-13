package laxy.server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "연관 태그 가중치 증가 요청 정보")
public class IncreaseTagAssociationRequest {

    @NotNull
    @Schema(description = "기존 태그 ID")
    private Long originalTagId;

    @NotNull
    @Schema(description = "연관 태그 ID")
    private Long relatedTagId;
}
