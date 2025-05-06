package com.nhnacademy.ruleengineservice.handler.action;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
import com.nhnacademy.ruleengineservice.exception.action.ActionHandlerException;
import com.nhnacademy.ruleengineservice.handler.ActionHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class ComfortNotificationActionHandler implements ActionHandler {
    @Override
    public boolean supports(String actType) {
        return "COMFORT_NOTIFICATION".equals(actType);
    }

    @Override
    public ActionResult handle(Action action, Map<String, Object> context) throws ActionHandlerException {
        String location = (String) context.get("location");
        Double comfortIndex = (Double) context.get("comfortIndex");
        String comfortGrade = (String) context.get("comfortGrade");

        ComfortInfoDTO comfortInfo = new ComfortInfoDTO(
                location,
                LocalDateTime.now(),
                comfortIndex,
                comfortGrade
        );

        return new ActionResult(
                action.getActNo(),
                true,
                action.getActType(),
                "쾌적도 알림 전송 성공",
                comfortInfo,
                LocalDateTime.now()
        );
    }
}
