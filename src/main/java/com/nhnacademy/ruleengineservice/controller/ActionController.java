package com.nhnacademy.ruleengineservice.controller;

import com.nhnacademy.ruleengineservice.dto.action.ActionRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.action.ActionResponse;
import com.nhnacademy.ruleengineservice.service.action.ActionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 액션(Action) 관련 HTTP 요청을 처리하는 컨트롤러입니다.
 * 액션 등록, 조회, 삭제 기능을 제공합니다.
 *
 * @author 강승우
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/actions")
public class ActionController {
    private final ActionService actionService;

    /**
     * 새로운 액션을 등록합니다.
     *
     * @param request 등록할 액션의 정보를 담은 요청 DTO ({@link ActionRegisterRequest})
     * @return 등록된 액션 정보를 담은 응답 DTO ({@link ActionResponse})
     */
    @PostMapping
    public ResponseEntity<ActionResponse> registerAction(@Valid @RequestBody ActionRegisterRequest request) {
        ActionResponse response = actionService.registerAction(request);

        log.debug("register action : {}", response);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * 특정 액션을 조회합니다.
     *
     * @param actNo 조회할 액션의 식별자
     * @return 조회된 액션 정보를 담은 응답 DTO ({@link ActionResponse})
     */
    @GetMapping("/{actNo}")
    public ResponseEntity<ActionResponse> getAction(@PathVariable("actNo") Long actNo) {
        ActionResponse response = actionService.getAction(actNo);

        log.debug("get Action : {}", response);

        return ResponseEntity.ok(response);
    }

    /**
     * 모든 액션 정보를 조회합니다.
     *
     * @return 액션 응답 정보 리스트와 HTTP 200 OK
     */
    @GetMapping
    public ResponseEntity<List<ActionResponse>> getActions() {
        List<ActionResponse> responses = actionService.getActions();

        log.debug("get actions : {}", responses);

        return ResponseEntity.ok(responses);
    }

    /**
     * 특정 규칙에 연결된 액션 정보를 조회합니다.
     *
     * @param ruleNo 조회할 규칙의 식별자
     * @return 해당 규칙에 연결된 액션 응답 정보 리스트와 HTTP 200 OK
     */
    @GetMapping("/rule/{ruleNo}")
    public ResponseEntity<List<ActionResponse>> getActionByRule(@PathVariable("ruleNo") Long ruleNo) {
        List<ActionResponse> responses = actionService.getActionsByRule(ruleNo);

        log.debug("get action by rule : {}", responses);

        return ResponseEntity.ok(responses);
    }

    /**
     * 특정 액션을 삭제합니다.
     *
     * @param actNo 삭제할 액션의 식별자
     * @return 내용이 없는 HTTP 응답 (204 No Content)
     */
    @DeleteMapping("/{actNo}")
    public ResponseEntity<Void> deleteAction(@PathVariable("actNo") Long actNo) {
        actionService.deleteAction(actNo);

        log.debug("action delete success");

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/rule/{ruleNo}")
    public ResponseEntity<Void> deleteActionByRule(@PathVariable("ruleNo") Long ruleNo) {
        actionService.deleteActionByRule(ruleNo);

        log.debug("Deleted all actions in rule : {}", ruleNo);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/rule/{ruleNo}/action/{actionNo}")
    public ResponseEntity<Void> deleteActionsByRuleNoAndActionNo (
            @PathVariable("ruleNo") Long ruleNo,
            @PathVariable("actionNo") Long actionNo
    ){
        actionService.deleteActionByRuleNoAndActionNo(ruleNo, actionNo);

        log.debug("Deleted action {} in rule {}", actionNo, ruleNo);

        return ResponseEntity.noContent().build();
    }
}
