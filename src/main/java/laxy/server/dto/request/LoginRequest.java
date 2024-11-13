package laxy.server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "로그인 요청 정보")
public class LoginRequest {

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Schema(description = "이메일")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Schema(description = "비밀번호")
    private String password;
}
