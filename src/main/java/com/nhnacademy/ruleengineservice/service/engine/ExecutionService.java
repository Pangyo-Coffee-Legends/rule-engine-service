package com.nhnacademy.ruleengineservice.service.engine;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;

import java.util.List;
import java.util.Map;

/**
 * 실행(Execution) 도메인 객체의 비즈니스 로직을 담당하는 서비스 인터페이스입니다.
 * <p>
 * 이 인터페이스는 룰 평가 결과 조건이 충족된 경우,
 * 해당 룰에 연결된 액션들을 일괄 실행(perform)하고,
 * 실행 결과(ActionResult)를 반환하는 핵심 기능을 정의합니다.
 * </p>
 * <p>
 * 주요 역할:
 * <ul>
 *   <li>하나의 룰에 연결된 여러 액션의 일괄 실행 및 실행 흐름 관리</li>
 *   <li>단일 액션 실행(performAction) 위임 및 실행 결과 취합</li>
 *   <li>실행 결과 로깅, 트랜잭션, 실행 정책(우선순위, 중단 조건 등) 적용</li>
 *   <li>API Rule-Engine과의 연동(조건 충족 시 액션 실행 위임)</li>
 * </ul>
 * </p>
 *
 * @author 강승우
 */
public interface ExecutionService {

    /**
     * 하나의 룰에 대한 액션 실행
     * @param rule 평가가 완료된 룰 객체
     * @param facts 액션 실행에 필요한 입력 데이터
     * @return 액션 실행 결과 리스트
     */
    List<ActionResult> executeActions(Rule rule, Map<String, Object> facts);

    /**
     * 단일 액션 실행
     * @param action 액션 객체
     * @param context 실행 파라미터(예: 이메일 정보, API 요청 정보 등)
     * @return 액션 실행 결과
     */
    ActionResult executeAction(Action action, Map<String, Object> context);
}
