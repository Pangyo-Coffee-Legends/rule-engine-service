package com.nhnacademy.ruleengineservice.config;

import com.nhnacademy.ruleengineservice.service.rule.RuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(WebMvcConfig.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class WebMvcConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RuleService ruleService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext(); // SecurityContext 초기화
    }

    @Test
    @DisplayName("인터셉터 제외 경로는 인터셉터가 동작하지 않는다")
    void excludePathPatternsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/comfort/test"))
                .andExpect(MockMvcResultMatchers.status().isNotFound()); // 실제 컨트롤러가 없으므로 404, 인터셉터 예외 안남
    }

    @Test
    @DisplayName("인터셉터가 적용되는 경로에서는 403 Forbidden을 반환한다")
    void interceptorAppliedPathTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/other/test"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @DisplayName("Preflight 요청")
    void testCorsPreflightRequest() throws Exception {
        mockMvc.perform(options("/api/v1/rules")
                        .header("Origin", "https://aiot2.live")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://aiot2.live"))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS"))
                .andExpect(header().string("Access-Control-Allow-Headers", "content-type"));
    }

    @Test
    @DisplayName("실제 GET 요청 테스트")
    void testCorsGetRequest() throws Exception {
        when(ruleService.getAllRule()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/rules")
                .header("Origin", "https://aiot2.live"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://aiot2.live"));

        verify(ruleService).getAllRule();
    }
}