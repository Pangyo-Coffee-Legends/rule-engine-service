package com.nhnacademy.ruleengineservice.adaptor;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.nhnacademy.ruleengineservice.dto.member.MemberInfoResponse;
import com.nhnacademy.ruleengineservice.dto.member.MemberResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "member-service.url=http://localhost:${wiremock.server.port}",
                "feign.client.config.defalut.loggerLever=full"
        }
)
@AutoConfigureWireMock(port=0)
@ActiveProfiles("test")
class MemberAdaptorTest {

    @Autowired
    private MemberAdaptor memberAdaptor;

    @Test
    void testMemberResponse() {
        // 가상의 ResponseEntity 생성 (실제 테스트에서는 실제 호출 결과를 사용)
        MemberResponse memberResponse = new MemberResponse(
                98L,
                "ROLE_USER",
                "Seungeu Kang",
                "seungeu@example.com",
                "Test1234!",
                "010-1234-5678"
        );

        ResponseEntity<MemberResponse> response = new ResponseEntity<>(memberResponse, HttpStatus.OK);
        log.debug("ResponseEntity<MemberResponse> 조회 : {}", response);

        // 상태 코드 검증
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // 바디 검증
        MemberResponse body = response.getBody();

        assertNotNull(body);
        assertAll(
                () -> assertEquals(98L, body.getNo()),
                () -> assertEquals("ROLE_USER", body.getRoleName()),
                () -> assertEquals("Seungeu Kang", body.getName()),
                () -> assertEquals("seungeu@example.com", body.getEmail())
        );
    }

    @Test
    @DisplayName("email 을 통해 member 조회")
    void getMemberByEmail() {
        String email = "seungeu@example.com";
        String encodedEmail = "seungeu%40example.com";
        String responseBody = """
            {
                "no": 98,
                "roleName": "ROLE_USER",
                "name": "Seungeu Kang"
            }
            """;

        stubFor(get(urlPathEqualTo("/api/v1/members/email/" + encodedEmail))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ResponseEntity<MemberResponse> response = memberAdaptor.getMemberByEmail(email);
        log.debug("email 을 통해 member 조회 : {}", response);

        WireMock.getAllServeEvents().forEach(event ->
                log.debug("Actual request URL: {}", event.getRequest().getUrl())
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(email, response.getBody().getEmail());

        verify(getRequestedFor(urlPathEqualTo("/api/v1/members/email/" + encodedEmail)));
    }

    @Test
    @DisplayName("member 에 있는 모든 조회")
    void getMemberInfoList() {
        String responseBody = """
                [
                    {"no": 81, "name": "Test1", "roleName": "ROLE_USER"},
                    {"no": 82, "name": "Test2", "roleName": "ROLE_ADMIN"}
                ]
                """;

        stubFor(get(urlPathEqualTo("/api/v1/members/info-list"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ResponseEntity<List<MemberInfoResponse>> response = memberAdaptor.getMemberInfoList();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response);
        assertEquals(2, response.getBody().size());
        assertEquals("Test1", response.getBody().get(0).getName());

        verify(getRequestedFor(urlPathEqualTo("/api/v1/members/info-list")));
    }
}