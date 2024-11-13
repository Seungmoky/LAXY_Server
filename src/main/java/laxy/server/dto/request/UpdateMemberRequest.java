package laxy.server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "회원 정보 수정 요청 정보, 수정하고자 하는 정보만 입력해주세요.")
public class UpdateMemberRequest {

    @Schema(description = "이름(닉네임)")
    private String name;

    @Schema(description = "비밀번호")
    private String password;

    @Schema(description = "생년월일 yyyy-MM-dd 형식으로 넣어주세요. ex) 2024-01-01")
    private String birth; // 생년월일 yyyy-MM-dd

    @Schema(description = "성별 [남자, 여자, 비공개] 중 하나를 넣어주세요.")
    private String gender; // 성별 [남자, 여자, 비공개]
}