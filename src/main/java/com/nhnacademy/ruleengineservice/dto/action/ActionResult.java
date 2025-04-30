package com.nhnacademy.ruleengineservice.dto.action;

import lombok.Value;

import java.time.LocalDateTime;

/**
 * 액션 실행 결과를 담는 DTO 클래스입니다.
 * 한 액션의 실행 성공/실패, 메시지, 출력값, 예외 정보를 포함합니다.
 */
@Value
public class ActionResult {

    /**
     * 액션의 식별자
     */
    Long actNo;

    /**
     * 액션 실행 성공 여부
     */
    boolean success;

    /**
     * 액션의 유형 (예: EMAIL, PUSH 등)
     */
    String actType;

    /**
     * 실행 결과 메시지 (예: "이메일 발송 성공", "푸시 실패" 등)
     */
    String message;

    /**
     * 액션 실행 결과 값 (예: 발송ID, 리턴값 등. 필요시)
     */
    Object output;

    /**
     * 액션 실행 시각
     */
    LocalDateTime executedAt;

}
