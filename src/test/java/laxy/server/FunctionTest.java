package laxy.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import laxy.server.repository.MemberRepository;
import laxy.server.service.PostService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class FunctionTest {

    @Mock
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private PostService postService;

    @Disabled
    @Test
    public void testExtractThumbnailUrl() {
        String jsonInput = "[{\"insert\":{\"image\":\"https://cafeptthumb-phinf.pstatic.net/MjAyNDA5MTdfMTU0/MDAxNzI2NTE2MDg1NTk5.M_iay-1R8pS0EJ-gByASWK3uIvlzsAgwiOwe2q3Qn0kg.DwXRVt7bVLCKq-b6RdxpGVeMAMNDgvNSA5C8VC1O1eUg.JPEG/Generic-iOS-18-Feature-Real-Mock.jpg?type=w1600\"}},{\"insert\":\"\\n\\n\"},{\"insert\":\"iOS 18의 새로운 기능들을 정리해봤습니다.\",\"attributes\":{\"bold\":true}},{\"insert\":\"\\n\\n직접 해보면서 간단하게 \"},{\"insert\":\"요약정리한\",\"attributes\":{\"background\":\"#FFFFF59D\"}},{\"insert\":\" 것이니 참조만 해주세요~\\n\\n\"},{\"insert\":{\"image\":\"https://cafeptthumb-phinf.pstatic.net/MjAyNDA5MTdfNTUg/MDAxNzI2NTA4NzY0MDU2.lemsm24MuabUk8kJLYgM0OwHd6bSMdPtoSbWAP2zyYcg.Ljivq_OtVuadsoz-PTgDUHKCgvDRUl1b1UPMmNU1MW8g.PNG/%EC%95%94%ED%98%B8_%EC%95%B1%2C_%EB%B0%B0%EC%97%B4.png?type=w1600\"},\"attributes\":{\"style\":\"width: 392.72727272727275; height: 310.176; \"}},{\"insert\":\" \"},{\"insert\":\"ios 18 \",\"attributes\":{\"color\":\"#FF1E88E5\"}},{\"insert\":\"에서는 홈화면을 자유롭게 꾸밀 수 있습니다.\\n새로운 홈화면 꾸미기\"},{\"insert\":\"\\n\",\"attributes\":{\"blockquote\":true}},{\"insert\":\"앱 배열 사용자화\"},{\"insert\":\"\\n\",\"attributes\":{\"list\":\"bullet\"}},{\"insert\":\"암호앱\"},{\"insert\":\"\\n\",\"attributes\":{\"list\":\"bullet\"}},{\"insert\":\"앱 색상 변경\"},{\"insert\":\"\\n\",\"attributes\":{\"list\":\"bullet\"}},{\"insert\":\"앱과 위젯을 자유롭게 변환하기\"},{\"insert\":\"\\n\",\"attributes\":{\"list\":\"bullet\"}},{\"insert\":\"잠금화면 설정 기능 선택, 액션 버튼의 기능 다양화\"},{\"insert\":\"\\n\",\"attributes\":{\"indent\":1,\"list\":\"bullet\"}},{\"insert\":{\"image\":\"https://cafeptthumb-phinf.pstatic.net/MjAyNDA5MTdfMTI2/MDAxNzI2NTEwMDA3NDAy.0_rVzocXqv7x2VBYMFs1xVM_KXgGUX9R56lEf747YQMg.1uOPiLe74NdSdwFfYDk4aUkfW9owWj4x76_ui9ewsFUg.PNG/KakaoTalk_20240917_030630972.png?type=w1600\"},\"attributes\":{\"style\":\"width: 392.72727272727275; height: 299.2101818181818; \"}},{\"insert\":\"제어센터 기능의 다양함\\n설정 - 연결된 에어팟 선택 - 머리 제스처 켬\\n\\n끄덕끄덕🙂‍↕️ 도리도리🙂‍↔️로 걸려오는 전화를 수신 및 거부할 수 있습니다.\\n\\n\"},{\"insert\":\"블로그 출처\",\"attributes\":{\"link\":\"https://cafe.naver.com/appleiphone/8515120\"}},{\"insert\":\"\\n\\n\"}]";

        String expectedUrl = "https://cafeptthumb-phinf.pstatic.net/MjAyNDA5MTdfMTU0/MDAxNzI2NTE2MDg1NTk5.M_iay-1R8pS0EJ-gByASWK3uIvlzsAgwiOwe2q3Qn0kg.DwXRVt7bVLCKq-b6RdxpGVeMAMNDgvNSA5C8VC1O1eUg.JPEG/Generic-iOS-18-Feature-Real-Mock.jpg?type=w1600";

        String thumbnailUrl = postService.extractThumbnailUrl(jsonInput);
        assertEquals(expectedUrl, thumbnailUrl);
    }
}
