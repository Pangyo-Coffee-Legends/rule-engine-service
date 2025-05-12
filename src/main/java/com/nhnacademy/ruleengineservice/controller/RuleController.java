package com.nhnacademy.ruleengineservice.controller;

import com.nhnacademy.ruleengineservice.dto.rule.RuleRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.rule.RuleResponse;
import com.nhnacademy.ruleengineservice.dto.rule.RuleUpdateRequest;
import com.nhnacademy.ruleengineservice.service.rule.RuleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 규칙(Rule) 관련 API를 제공하는 컨트롤러입니다.
 * <p>
 * 규칙 등록, 조회, 수정, 삭제 등 Rule 도메인에 대한 HTTP 엔드포인트를 제공합니다.
 * </p>
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/rules")
public class RuleController {

    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    /**
     * 새로운 규칙을 등록합니다.
     *
     * @param request 규칙 등록 요청 DTO
     * @return 등록된 규칙 정보
     */
    @PostMapping
    public ResponseEntity<RuleResponse> registerRule(
            @Valid @RequestBody RuleRegisterRequest request
    ) {
        RuleResponse response = ruleService.registerRule(request);

        log.debug("registerRule : {}", response);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * 규칙을 단건 조회합니다.
     *
     * @param ruleNo 규칙 식별자
     * @return 규칙 정보
     */
    @GetMapping("/{ruleNo}")
    public ResponseEntity<RuleResponse> getRule(@PathVariable Long ruleNo) {
        RuleResponse response = ruleService.getRule(ruleNo);

        log.debug("getRule : {}", response);

        return ResponseEntity.ok(response);
    }

    /**
     * 규칙 목록을 조회합니다.
     *
     * @return 규칙 리스트
     */
    @GetMapping
    public ResponseEntity<List<RuleResponse>> getRules() {
        List<RuleResponse> rules = ruleService.getAllRule();

        log.debug("getRules : {}", rules);

        return ResponseEntity.ok(rules);
    }

    /**
     * 규칙을 수정합니다.
     *
     * @param ruleNo  규칙 식별자
     * @param request 규칙 수정 요청 DTO
     * @return 수정된 규칙 정보
     */
    @PutMapping("/{ruleNo}")
    public ResponseEntity<RuleResponse> updateRule(@PathVariable Long ruleNo,
                                                   @RequestBody RuleUpdateRequest request) {
        RuleResponse response = ruleService.updateRule(ruleNo, request);

        log.debug("updateRule : {}", response);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(response);
    }

    /**
     * 규칙을 삭제합니다.
     *
     * @param ruleNo 규칙 식별자
     * @return 성공 여부
     */
    @DeleteMapping("/{ruleNo}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long ruleNo) {
        ruleService.deleteRule(ruleNo);

        log.debug("deleteRule run");

        return ResponseEntity.noContent().build();
    }
}
