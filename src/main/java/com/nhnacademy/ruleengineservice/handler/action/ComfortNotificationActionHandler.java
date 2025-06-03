package com.nhnacademy.ruleengineservice.handler.action;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.dto.action.ActionResponse;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.exception.action.ActionHandlerException;
import com.nhnacademy.ruleengineservice.handler.ActionHandler;
import com.nhnacademy.ruleengineservice.service.action.ActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class ComfortNotificationActionHandler implements ActionHandler {

    @Lazy
    private final ActionService actionService;

    @Override
    public boolean supports(String actType) {
        return "COMFORT_NOTIFICATION".equals(actType);
    }

    @Override
    public ActionResult handle(Action action, Map<String, Object> context) throws ActionHandlerException {
        String comportIndex = (String) context.get("comport_index");
        if (Objects.nonNull(comportIndex)) {
            // aircon : 냉방기, dehumidifier: 제습기, heater: 난방기, humidifier: 가습기
            Map<String, Boolean> deviceCommands = switch (comportIndex) {
                case "덥고 습함" -> Map.of("aircon", true, "dehumidifier", true);
                case "춥고 건조" -> Map.of("heater", true, "humidifier", true);
                case "최적 쾌적" -> Map.of("aircon", false, "heater", false);
                default -> Map.of();
            };

            context.putAll(deviceCommands);
        }

        // ventilator : 환풍기
        String co2Comment = (String) context.get("co2_comment");
        if (Objects.nonNull(co2Comment)) {
            context.put("ventilator", "CO2 주의".equals(co2Comment));
        }

        ActionResponse response = actionService.getAction(action.getActNo());

        if (Objects.nonNull(response)) {
            context.put("action", response.getActParam());
        }

        log.debug("context : {}", context);

        return new ActionResult(
                action.getActNo(),
                true,
                action.getActType(),
                "쾌적도 알림 전송 성공",
                context,
                LocalDateTime.now()
        );
    }
}
