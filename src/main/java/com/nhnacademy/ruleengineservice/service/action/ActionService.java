package com.nhnacademy.ruleengineservice.service.action;

import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.dto.action.ActionRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.action.ActionResponse;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;

import java.util.List;
import java.util.Map;

/**
 * 액션(Action) 도메인 객체의 비즈니스 로직을 담당하는 서비스 인터페이스입니다.
 * <p>
 * 이 인터페이스는 actions 테이블의 CRUD 연산, 룰과 연결된 액션 관리,
 * 액션 실행(EMAIL, PUSH, LOG 등)과 관련된 핵심 비즈니스 기능을 정의합니다.
 * </p>
 * <p>
 * 주요 역할:
 * <ul>
 *   <li>액션 등록, 수정, 삭제, 단건/목록 조회</li>
 *   <li>특정 룰에 연결된 액션 목록 조회</li>
 *   <li>액션 실행(performAction), 액션 실행 결과 반환</li>
 *   <li>룰 엔진과의 연동(조건 충족 시 액션 일괄 실행 등)</li>
 * </ul>
 * </p>
 *
 * @author 강승우
 */
public interface ActionService {

    /**
     * 새로운 액션을 등록합니다.
     * @param request 액션 등록 요청 DTO
     * @return 생성된 액션의 DTO
     */
    ActionResponse registerAction(ActionRegisterRequest request);

    /**
     * 액션을 삭제합니다.
     * @param actionNo 액션 식별자
     */
    void deleteAction(Long actionNo);

    /**
     * 액션 단건 조회
     * @param actionNo 액션 식별자
     * @return 액션 상세 DTO
     */
    ActionResponse getAction(Long actionNo);

    /**
     * 특정 규칙에 연결된 액션 목록 조회
     * @param ruleNo 규칙 식별자
     * @return 액션 응답 DTO 리스트
     */
    List<ActionResponse> getActionsByRule(Long ruleNo);

    /**
     * 액션 실행(perform)
     * @param actionNo 액션 식별자
     * @param context 실행에 필요한 데이터(파라미터)
     * @return 실행 결과(성공/실패, 메시지 등)
     */
    ActionResult performAction(Long actionNo, Map<String, Object> context);

    /**
     * 특정 룰에 연결된 모든 액션을 실행합니다.
     * <p>
     * 이 메서드는 룰에 연결된 모든 액션을 가져와 실행하고,
     * 각 액션의 실행 결과를 ActionResult 객체 리스트로 반환합니다.
     * 룰 조건이 충족되었을 때 RuleEngineService에서 호출됩니다.
     * </p>
     *
     * @param rule    실행할 액션이 연결된 룰 객체
     * @param context 액션 실행에 필요한 컨텍스트 데이터
     * @return 액션 실행 결과 목록 (ActionResult 리스트)
     */
    List<ActionResult> executeActionsForRule(Rule rule, Map<String, Object> context);
}
