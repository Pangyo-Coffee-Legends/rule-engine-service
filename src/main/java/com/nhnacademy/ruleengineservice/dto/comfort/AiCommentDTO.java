package com.nhnacademy.ruleengineservice.dto.comfort;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AiCommentDTO {
    /**
     * 온도
     */
    String temperature;

    /**
     * 습도
     */
    String humidity;

    /**
     * co2
     */
    String co2;
}
