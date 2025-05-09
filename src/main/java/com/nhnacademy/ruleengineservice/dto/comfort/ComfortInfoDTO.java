package com.nhnacademy.ruleengineservice.dto.comfort;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * 쾌적도 정보를 담아서 보내는 DTO 클래스
 */
@Value
public class ComfortInfoDTO {
    /**
     * 장소
     */
    String location;

    /**
     * 현재 시간
     */
    LocalDateTime currentTime;

    /**
     * 온도
     */
    Double temperature;

    /**
     * 습도
     */
    Double humidity;

    /**
     * co2
     */
    Double co2;

    /**
     * ai 온도, 습도 설명
     */
    @JsonProperty("comport-index")
    String comportIndex;

    /**
     * ai co2 설명
     */
    @JsonProperty("co2-comment")
    String co2Comment;
}
