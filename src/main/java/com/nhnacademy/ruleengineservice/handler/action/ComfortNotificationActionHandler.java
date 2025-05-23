package com.nhnacademy.ruleengineservice.handler.action;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
import com.nhnacademy.ruleengineservice.exception.action.ActionHandlerException;
import com.nhnacademy.ruleengineservice.handler.ActionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ComfortNotificationActionHandler implements ActionHandler {

    // 디바이스 타입 상수
    private static final String AIRCON = "aircon";
    private static final String DEHUMIDIFIER = "dehumidifier";
    private static final String HEATER = "heater";
    private static final String HUMIDIFIER = "humidifier";
    private static final String VENTILATOR = "ventilator";

    // 쾌적 지수 상수
    private static final String HOT_HUMID = "덥고 습함";
    private static final String COLD_DRY = "춥고 건조";
    private static final String OPTIMAL = "최적 쾌적";

    @Override
    public boolean supports(String actType) {
        return "COMFORT_NOTIFICATION".equals(actType);
    }

    @Override
    public ActionResult handle(Action action, Map<String, Object> context) throws ActionHandlerException {
        // 1. context 에서 ComfortInfoDTO 추출
        ComfortInfoDTO comfortInfo = (ComfortInfoDTO) context.get("comfortInfo");
        if (comfortInfo == null) {
            log.error("[액션 실패] 액션 ID {} - ComfortInfoDTO 누락", action.getActNo());
            return createErrorResult(action, "ComfortInfoDTO 누락");
        }

        // 2. 쾌적 지수 null 체크
        if (comfortInfo.getComfortIndex() == null) {
            log.warn("쾌적 지수가 설정되지 않았습니다. 액션 ID: {}", action.getActNo());
        }

        // 3. device 명령 생성
        Map<String, Boolean> deviceCommands = generateDeviceCommands(comfortInfo);
        log.debug("[액션 실행] 액션 ID {} - 장치 명령: {}", action.getActNo(), deviceCommands);

        // 4. 출력 데이터 구성
        Map<String, Object> output = Map.of(
                "deviceCommands", deviceCommands,
                "comfortInfo", comfortInfo
        );

        return new ActionResult(
                action.getActNo(),
                true,
                action.getActType(),
                "쾌적도 알림 전송 성공",
                output,
                LocalDateTime.now()
        );
    }

    private Map<String, Boolean> generateDeviceCommands(ComfortInfoDTO comfortInfo) {
        Map<String, Boolean> commands = new HashMap<>();

        if (comfortInfo.getComfortIndex() != null) {
            switch (comfortInfo.getComfortIndex()) {
                case HOT_HUMID -> {
                    commands.put(AIRCON, true);
                    commands.put(DEHUMIDIFIER, true);
                }
                case COLD_DRY -> {
                    commands.put(HEATER, true);
                    commands.put(HUMIDIFIER, true);
                }
                case OPTIMAL -> {
                    commands.put(AIRCON, false);
                    commands.put(HEATER, false);
                }
                default -> log.warn("지원되지 않는 쾌적 지수: {}", comfortInfo.getComfortIndex());
            }
        }

        commands.put(VENTILATOR, "CO2 주의".equals(comfortInfo.getCo2Comment()));

        return commands;
    }

    private ActionResult createErrorResult(Action action, String message) {
        return new ActionResult(
                action.getActNo(),
                false,
                action.getActType(),
                message,
                null,
                LocalDateTime.now()
        );
    }
}
