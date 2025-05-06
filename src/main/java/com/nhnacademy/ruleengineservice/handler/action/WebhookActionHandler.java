package com.nhnacademy.ruleengineservice.handler.action;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.exception.action.ActionHandlerException;
import com.nhnacademy.ruleengineservice.handler.ActionHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class WebhookActionHandler implements ActionHandler {
    @Override
    public boolean supports(String actType) {
        return "WEBHOOK".equals(actType);
    }

    @Override
    public ActionResult handle(Action action, Map<String, Object> context) throws ActionHandlerException {
        return new ActionResult(
                action.getActNo(),
                true,
                action.getActType(),
                "웹훅 호출 성공",
                null,
                LocalDateTime.now()
        );
    }
}
