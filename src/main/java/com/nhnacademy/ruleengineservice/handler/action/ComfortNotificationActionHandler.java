package com.nhnacademy.ruleengineservice.handler.action;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
import com.nhnacademy.ruleengineservice.exception.action.ActionHandlerException;
import com.nhnacademy.ruleengineservice.handler.ActionHandler;
import com.nhnacademy.ruleengineservice.service.action.ActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
        // 1. 컨텍스트에서 ComfortInfoDTO 추출
        ComfortInfoDTO comfortInfo = (ComfortInfoDTO) context.get("comfortInfo");
        if (comfortInfo == null) {
            log.error("컨텍스트에 ComfortInfoDTO가 없습니다.");
            return new ActionResult(
                    action.getActNo(),
                    false,
                    action.getActType(),
                    "ComfortInfoDTO 누락",
                    null,
                    LocalDateTime.now()
            );
        }

        // 2. 디바이스 명령 생성 (기존 로직)
        Map<String, Boolean> deviceCommands = new HashMap<>();
        if (comfortInfo.getComfortIndex() != null) {
            switch (comfortInfo.getComfortIndex()) {
                case "덥고 습함" -> deviceCommands.putAll(Map.of("aircon", true, "dehumidifier", true));
                case "춥고 건조" -> deviceCommands.putAll(Map.of("heater", true, "humidifier", true));
                case "최적 쾌적" -> deviceCommands.putAll(Map.of("aircon", false, "heater", false));
                default -> deviceCommands.put("device", false);
            }
        }
        if ("CO2 주의".equals(comfortInfo.getCo2Comment())) {
            deviceCommands.put("ventilator", true);
        }

        // 3. 출력 데이터 구성 (ComfortInfoDTO 포함)
        Map<String, Object> output = new HashMap<>();
        output.put("deviceCommands", deviceCommands);
        output.put("comfortInfo", comfortInfo); // ComfortInfoDTO 추가

        return new ActionResult(
                action.getActNo(),
                true,
                action.getActType(),
                "쾌적도 알림 전송 성공",
                output,
                LocalDateTime.now()
        );
    }
}
