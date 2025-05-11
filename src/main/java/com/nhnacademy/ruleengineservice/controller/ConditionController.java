package com.nhnacademy.ruleengineservice.controller;

import com.nhnacademy.ruleengineservice.dto.condition.ConditionRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.condition.ConditionResponse;
import com.nhnacademy.ruleengineservice.service.condition.ConditionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ConditionResponse> registerCondition(@RequestBody ConditionRegisterRequest request) {
        ConditionResponse response = conditionService.registerCondition(request);

        log.debug("register condition : {}", response);

        return ResponseEntity.ok(response);
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
}
