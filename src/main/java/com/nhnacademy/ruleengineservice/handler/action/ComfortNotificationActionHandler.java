package com.nhnacademy.ruleengineservice.handler.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
import com.nhnacademy.ruleengineservice.dto.comfort.ComfortNotificationDTO;
import com.nhnacademy.ruleengineservice.exception.action.ActionHandlerException;
import com.nhnacademy.ruleengineservice.handler.ActionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ComfortNotificationActionHandler implements ActionHandler {
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(String actType) {
        return "COMFORT_NOTIFICATION".equals(actType);
    }

    @Override
    public ActionResult handle(Action action, Map<String, Object> context) throws ActionHandlerException {
        try {
            if (!context.containsKey("json")) {
                throw new ActionHandlerException("context 'json' not found");
            }

            String json = (String) context.get("json");
            ComfortNotificationDTO dto = objectMapper.readValue(json, ComfortNotificationDTO.class);

            // dto 에서 필요한 정보 추출
            String location = dto.getLocation();
            Double temp = dto.getComfortIndex().getTemperature();
            Double humidity = dto.getComfortIndex().getHumidity();
            Double co2 = dto.getComfortIndex().getCo2();

            String tempComment = dto.getAiComment().getTemperature();
            String humidityComment = dto.getAiComment().getHumidity();
            String co2Comment = dto.getAiComment().getCo2();

            ComfortInfoDTO comfortInfo = new ComfortInfoDTO(
                    location,
                    LocalDateTime.now(),
                    temp,
                    humidity,
                    co2,
                    tempComment,
                    humidityComment,
                    co2Comment
            );

            return new ActionResult(
                    action.getActNo(),
                    true,
                    action.getActType(),
                    "쾌적도 알림 전송 성공",
                    comfortInfo,
                    LocalDateTime.now()
            );
        } catch (JsonProcessingException | IllegalArgumentException e) {
            throw new ActionHandlerException("ComfortNotificationDTO 파싱 실패", e);
        }
    }
}
