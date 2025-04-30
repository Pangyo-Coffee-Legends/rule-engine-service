package com.nhnacademy.ruleengineservice.dto.comfort;

import java.time.LocalDateTime;

/**
 * 쾌적도 정보를 담아서 보내는 DTO 클래스
 */
public class ComfortInfoDTO {
    /**
     * 장소
     */
    private String location;

    /**
     * 현재 시간
     */
    private LocalDateTime currentTime;

    /**
     * 쾌적지수
     */
    private Double comfortIndex;

    /**
     * 쾌적지수 등급
     */
    private String comfortGrade;

    public ComfortInfoDTO(String location, Double comfortIndex, String comfortGrade) {
        this.location = location;
        this.currentTime = LocalDateTime.now();
        this.comfortIndex = comfortIndex;
        this.comfortGrade = comfortGrade;
    }
}
