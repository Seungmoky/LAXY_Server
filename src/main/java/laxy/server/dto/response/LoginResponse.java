package laxy.server.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "로그인 정보")
public class LoginResponse {

    @Schema(description = "엑세스 토큰")
    private String accessToken;

    @Schema(description = "리프레시 토큰")
    private String refreshToken;

    @Schema(description = "이름(닉네임)")
    private String name;

    @Schema(description = "이메일")
    private String email;

    public LoginResponse(String accessToken, String refreshToken, String name, String email) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.name = name;
        this.email = email;
    }
}
