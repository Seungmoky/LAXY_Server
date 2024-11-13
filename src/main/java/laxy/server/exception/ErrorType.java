package laxy.server.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorType {

    // 요청 관련 오류
    MISSING_HEADER("E001", "요청 헤더가 누락되었습니다."),
    INVALID_REQUEST_PARAMETER("E002", "잘못된 요청 파라미터입니다."),
    MISSING_REQUEST_PARAMETER("E003", "요청 파라미터가 누락되었습니다."),
    INVALID_REQUEST_BODY("E004", "잘못된 요청 본문입니다."),
    UNEXPECTED_ERROR("E005", "예기치 않은 오류가 발생하였습니다."),

    // JWT 인증 관련 오류
    JWT_TOKEN_NOT_FOUND("E101", "JWT 토큰을 찾을 수 없습니다."),
    INVALID_JWT_TOKEN("E102", "유효하지 않은 JWT 토큰입니다."),
    ACCESS_TOKEN_EXPIRED("E103", "Access Token이 만료되었습니다."),
    REFRESH_TOKEN_EXPIRED("E104", "Refresh Token이 만료되었습니다."),

    // OAuth 인증 관련 오류
    INVALID_AUTHORIZATION_CODE("E201", "잘못된 인증 코드입니다."),
    KAKAO_USER_INFO_REQUEST_FAILED("E202", "카카오 사용자 정보 요청에 실패했습니다."),
    NAVER_USER_INFO_REQUEST_FAILED("E203", "네이버 사용자 정보 요청에 실패했습니다."),
    GOOGLE_USER_INFO_REQUEST_FAILED("E204", "구글 사용자 정보 요청에 실패했습니다."),

    // 파일 관련 오류
    FILE_UPLOAD_ERROR("E301", "파일 업로드 중 오류가 발생하였습니다."),
    FILE_DELETE_ERROR("E302", "파일 삭제 중 오류가 발생하였습니다."),

    // 권한 관련 오류
    PERMISSION_DENIED("E401", "접근 권한이 없습니다."),

    // 리소스 관련 오류
    FILE_NOT_FOUND("E501", "해당 파일을 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS("E502", "이미 사용 중인 이메일입니다."),
    MEMBER_NOT_FOUND("E503", "해당 회원을 찾을 수 없습니다."),
    POST_NOT_FOUND("E504", "해당 게시물을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND("E505", "해당 댓글을 찾을 수 없습니다."),
    TAG_NOT_FOUND("E506", "해당 태그를 찾을 수 없습니다."),

    // 형식 관련 오류
    INVALID_DATE_FORMAT("E601", "올바른 날짜 형식이 아닙니다."),

    // 인증 관련
    INVALID_PASSWORD("E701", "비밀번호가 일치하지 않습니다."),
    INVALID_AUTHOR("E702", "작성자 본인이 아닙니다."),

    // 좋아요 북마크 관련
    ALREADY_LIKED("E801", "이미 좋아요 상태입니다."),
    NOT_LIKED("E802", "좋아요 상태가 아닙니다."),
    ALREADY_BOOKMARKED("E803", "이미 북마크 상태입니다."),
    NOT_BOOKMARKED("E804", "북마크 상태가 아닙니다."),

    // 태그 관련
    TAG_COUNT_TOO_LOW("E901", "태그는 최소 1개 이상이어야 합니다."),
    TAG_COUNT_TOO_HIGH("E902", "태그는 최대 10개까지만 허용됩니다. "),
    ;
    private final String code;
    private final String message;
}
