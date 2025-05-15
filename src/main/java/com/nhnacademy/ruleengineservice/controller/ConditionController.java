package com.nhnacademy.ruleengineservice.controller;

import com.nhnacademy.ruleengineservice.dto.condition.ConditionRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.condition.ConditionResponse;
import com.nhnacademy.ruleengineservice.service.condition.ConditionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 조건(Condition) 관련 HTTP 요청을 처리하는 컨트롤러입니다.
 * 조건 생성, 조회, 삭제 기능을 제공합니다.
 *
 * @author 강승우
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/conditions")
public class ConditionController {

    private final ConditionService conditionService;

    /**
     * 새로운 조건을 등록합니다.
     *
     * @param request 등록할 조건의 정보를 담은 요청 DTO
     * @return 등록된 조건 정보를 담은 응답 DTO
     * @throws IllegalArgumentException 유효하지 않은 입력 값이 전달된 경우
     */
    @PostMapping
    public ResponseEntity<ConditionResponse> registerCondition(@Valid @RequestBody ConditionRegisterRequest request) {
        ConditionResponse response = conditionService.registerCondition(request);

        log.debug("register condition : {}", response);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * 특정 조건을 조회합니다.
     *
     * @param conditionNo 조회할 조건의 식별자
     * @return 조회된 조건 정보를 담은 응답 DTO
     */
    @GetMapping("/{conditionNo}")
    public ResponseEntity<ConditionResponse> getCondition(@PathVariable("conditionNo") Long conditionNo) {
        ConditionResponse condition = conditionService.getCondition(conditionNo);

        log.debug("get Condition : {}", condition);

        return ResponseEntity
                .ok(condition);
    }

    /**
     * 모든 조건 정보를 조회합니다.
     *
     * @return HTTP 200 OK 상태와 함께 전체 조건 응답 정보 리스트를 반환합니다.
     */
    @GetMapping
    public ResponseEntity<List<ConditionResponse>> getConditions() {
        List<ConditionResponse> responses = conditionService.getConditions();

        log.debug("get conditions : {}", responses);

        return ResponseEntity.ok(responses);
    }

    /**
     * 특정 규칙에 연결된 조건 정보를 조회합니다.
     *
     * @param ruleNo 조회할 규칙의 고유 번호
     * @return HTTP 200 OK 상태와 함께 해당 규칙의 조건 응답 정보 리스트를 반환합니다.
     */
    @GetMapping("/rule/{ruleNo}")
    public ResponseEntity<List<ConditionResponse>> getConditionByRule(@PathVariable("ruleNo") Long ruleNo) {
        List<ConditionResponse> responses = conditionService.getConditionsByRule(ruleNo);

        log.debug("get condition by rule : {}", responses);

        return ResponseEntity.ok(responses);
    }

    /**
     * 특정 조건을 삭제합니다.
     *
     * @param conditionNo 삭제할 조건의 식별자
     * @return 내용이 없는 HTTP 응답 (204 No Content)
     */
    @DeleteMapping("/{conditionNo}")
    public ResponseEntity<Void> deleteCondition(@PathVariable("conditionNo") Long conditionNo) {
        conditionService.deleteCondition(conditionNo);

        log.debug("condition delete success");

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/rule/{ruleNo}")
    public ResponseEntity<Void> deleteConditionByRule(@PathVariable("ruleNo") Long ruleNo) {
        conditionService.deleteConditionByRule(ruleNo);

        log.debug("Deleted all conditions in rule : {}", ruleNo);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/rule/{ruleNo}/condition/{conditionNo}")
    public ResponseEntity<Void> deleteConditionByRuleNoAndConditionNo(
            @PathVariable("ruleNo") Long ruleNo,
            @PathVariable("conditionNo") Long conditionNo
    ) {
        conditionService.deleteConditionByRuleNoAndConditionNo(ruleNo, conditionNo);

        log.debug("Deleted condition {} in rule {}", conditionNo, ruleNo);

        return ResponseEntity.noContent().build();
    }
}
