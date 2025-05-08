package com.nhnacademy.ruleengineservice.dto.comfort;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ComfortIndexDTO {
    /**
     * 온도
     */
    Double temperature;

    /**
     * 습도
     */
    Double humidity;

    /**
     * Co2
     */
    Double co2;
}
