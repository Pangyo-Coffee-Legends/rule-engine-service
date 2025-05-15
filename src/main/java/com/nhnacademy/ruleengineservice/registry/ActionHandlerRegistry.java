package com.nhnacademy.ruleengineservice.registry;

import com.nhnacademy.ruleengineservice.exception.action.UnsupportedActionTypeException;
import com.nhnacademy.ruleengineservice.handler.ActionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 등록된 {@link ActionHandler} 구현체를 관리하고, 액션 타입(actType)에 맞는 핸들러를 조회하는 레지스트리 클래스입니다.
 * <p>
 * 이 클래스는 Spring의 의존성 주입을 통해 모든 {@link ActionHandler} 빈들을 수집하며,
 * 클라이언트 코드가 특정 액션 타입에 해당하는 핸들러를 검색할 때 사용됩니다.
 * </p>
 *
 * @author 강승우
 */
@Component
public class ActionHandlerRegistry {

    /**
     * 등록된 모든 {@link ActionHandler} 구현체 목록입니다.
     */
    private final List<ActionHandler> handlers;

    @Lazy
    @Autowired
    public ActionHandlerRegistry(List<ActionHandler> handlers) {
        this.handlers = handlers;
    }

    /**
     * 주어진 액션 타입(actType)을 지원하는 {@link ActionHandler}를 반환합니다.
     *
     * @param actType 핸들러를 조회할 액션 타입 (예: "EMAIL", "WEBHOOK")
     * @return 해당 액션 타입을 지원하는 {@link ActionHandler} 구현체
     * @throws UnsupportedActionTypeException 지원되지 않는 액션 타입이 전달된 경우
     */
    public ActionHandler getHandler(String actType) {
        return handlers.stream()
                .filter(handler -> handler.supports(actType))
                .findFirst()
                .orElseThrow(() -> new UnsupportedActionTypeException(actType));
    }
}
