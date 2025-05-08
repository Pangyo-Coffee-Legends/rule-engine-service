package com.nhnacademy.ruleengineservice.handler;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.exception.action.ActionHandlerException;

import java.util.Map;

/**
 * 다양한 액션 타입을 처리하는 핸들러의 공통 인터페이스입니다.
 * <p>
 * 각 구현체는 특정 액션 타입을 지원하며,
 * {@link #supports(String)} 메서드로 지원 여부를 판단하고,
 * {@link #handle(Action, Map)} 메서드로 실제 액션을 수행합니다.
 * </p>
 *
 * @author 강승우
 */
public interface ActionHandler {
    /**
     * 이 핸들러가 주어진 액션 타입을 지원하는지 여부를 반환합니다.
     *
     * @param actType 검사할 액션 타입 (예: "EMAIL", "WEBHOOK" 등)
     * @return 지원하면 {@code true}, 아니면 {@code false}
     */
    boolean supports(String actType);

    /**
     * 주어진 액션과 컨텍스트 정보를 기반으로 액션을 수행합니다.
     *
     * @param action  실행할 액션 객체
     * @param context 액션 실행에 필요한 추가 정보(컨텍스트)
     * @return 액션 실행 결과를 담은 {@link ActionResult} 객체
     * @throws ActionHandlerException 액션 처리 중 예외가 발생한 경우
     */
    ActionResult handle(Action action, Map<String, Object> context) throws ActionHandlerException;
}
