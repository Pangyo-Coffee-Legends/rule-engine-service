package com.nhnacademy.ruleengineservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
import com.nhnacademy.ruleengineservice.service.engine.RuleEngineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/comfort")
public class ComfortController {

    private final RuleEngineService ruleEngineService;
    private final ObjectMapper objectMapper;

    public ComfortController(RuleEngineService ruleEngineService, ObjectMapper objectMapper) {
        this.ruleEngineService = ruleEngineService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<String> receiveComfortInfo(@RequestBody ComfortInfoDTO comfortInfo) {
        log.debug("받은 정보: {}", comfortInfo);

        Map<String, Object> facts = objectMapper.convertValue(
                comfortInfo,
                new com.fasterxml.jackson.core.type.TypeReference<>() {}
        );

        ruleEngineService.executeTriggeredRules("AI_DATA_RECEIVED", "{\"source\":\"AI\"}", facts);

        return ResponseEntity.ok("데이터 정상 수신");
    }
}
