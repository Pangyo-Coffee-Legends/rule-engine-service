package com.nhnacademy.ruleengineservice.controller;

import com.nhnacademy.ruleengineservice.dto.rule.RuleGroupRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.rule.RuleGroupResponse;
import com.nhnacademy.ruleengineservice.service.rule.RuleGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 규칙 그룹(Rule Group) 관리 API를 제공하는 REST 컨트롤러입니다.
 * <p>
 * 이 컨트롤러는 rule_groups 테이블에 대한 CRUD 작업을 수행하며,
 * 규칙 그룹의 등록, 조회, 수정, 삭제 및 활성화 등 관리 기능을 HTTP 엔드포인트로 제공합니다.
 * <p>
 * 주요 역할:
 * <ul>
 *   <li>새로운 규칙 그룹 등록 (POST /api/v1/rule-groups)</li>
 *   <li>특정 규칙 그룹 조회 (GET /api/v1/rule-groups/{ruleGroupNo})</li>
 *   <li>모든 규칙 그룹 조회 (GET /api/v1/rule-groups)</li>
 *   <li>규칙 그룹 삭제 (DELETE /api/v1/rule-groups/{ruleGroupNo})</li>
 * </ul>
 * </p>
 * <p>
 * 규칙 그룹은 여러 개의 규칙(Rule)을 포함할 수 있으며,
 * 이 컨트롤러는 API Rule-Engine, API 예약, AI LSTM 등의 다른 컴포넌트와
 * 함께 전체 시스템의 룰 기반 자동화를 지원합니다.
 * </p>
 *
 * @author 강승우
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/rule-groups")
public class RuleGroupController {

    private final RuleGroupService ruleGroupService;

    public RuleGroupController(RuleGroupService ruleGroupService) {
        this.ruleGroupService = ruleGroupService;
    }

    /**
     * 새로운 규칙 그룹을 등록합니다.
     *
     * @param request 규칙 그룹 등록 요청 DTO
     * @return 등록된 규칙 그룹 정보
     */
    @PostMapping
    public ResponseEntity<RuleGroupResponse> registerRuleGroup(@RequestBody RuleGroupRegisterRequest request) {
        RuleGroupResponse response = ruleGroupService.registerRuleGroup(request);

        log.debug("registerRuleGroup : {}", response);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 규칙 그룹을 단건 조회합니다.
     *
     * @param ruleGroupNo 규칙 그룹 식별자
     * @return 규칙 그룹 정보
     */
    @GetMapping("/{ruleGroupNo}")
    public ResponseEntity<RuleGroupResponse> getRuleGroup(@PathVariable Long ruleGroupNo) {
        RuleGroupResponse response = ruleGroupService.getRuleGroup(ruleGroupNo);

        log.debug("getRuleGroup : {}", response);

        return ResponseEntity.ok(response);
    }

    /**
     * 모든 규칙 그룹 목록을 조회합니다.
     *
     * @return 규칙 그룹 리스트
     */
    @GetMapping
    public ResponseEntity<List<RuleGroupResponse>> getRuleGroups() {
        List<RuleGroupResponse> groups = ruleGroupService.getAllRuleGroups();

        log.debug("getRuleGroups : {}", groups);

        return ResponseEntity.ok(groups);
    }

    /**
     * 규칙 그룹을 삭제합니다.
     *
     * @param ruleGroupNo 규칙 그룹 식별자
     * @return 성공 여부
     */
    @DeleteMapping("/{ruleGroupNo}")
    public ResponseEntity<Void> deleteRuleGroup(@PathVariable Long ruleGroupNo) {
        ruleGroupService.deleteRuleGroup(ruleGroupNo);

        log.debug("deleteRuleGroup run");

        return ResponseEntity.noContent().build();
    }
}
