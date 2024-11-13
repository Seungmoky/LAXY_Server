package laxy.server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "회원 가입 요청 정보")
public class SignUpRequest {

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Schema(description = "이메일")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Schema(description = "비밀번호")
    private String password;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Schema(description = "이름(닉네임)")
    private String name; // 회원 이름

    @NotBlank(message = "생년월일은 필수 입력 값입니다.")
    @Schema(description = "생년월일 yyyy-MM-dd 형식으로 넣어주세요. ex) 2024-01-01")
    private String birth; // 생년월일 yyyy-MM-dd

    @NotBlank(message = "성별은 필수 입력 값입니다.")
    @Schema(description = "성별 [남자, 여자, 비공개] 중 하나를 넣어주세요.")
    private String gender; // 성별 [남자, 여자, 비공개]
}
