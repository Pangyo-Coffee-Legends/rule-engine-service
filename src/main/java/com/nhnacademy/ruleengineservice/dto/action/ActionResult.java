package com.nhnacademy.ruleengineservice.dto.action;

import java.time.LocalDateTime;

/**
 * 액션 실행 결과를 담는 DTO 클래스입니다.
 * 한 액션의 실행 성공/실패, 메시지, 출력값, 예외 정보를 포함합니다.
 */
public class ActionResult {

    /**
     * 액션의 식별자
     */
    private Long actNo;

    /**
     * 액션 실행 성공 여부
     */
    private boolean success;

    /**
     * 액션의 유형 (예: EMAIL, PUSH 등)
     */
    private String actType;

    /**
     * 실행 결과 메시지 (예: "이메일 발송 성공", "푸시 실패" 등)
     */
    private String message;

    /**
     * 액션 실행 결과 값 (예: 발송ID, 리턴값 등. 필요시)
     */
    private Object output;

    /**
     * 액션 실행 시각
     */
    private LocalDateTime executedAt;

    /**
     * Action Result DTO 객체의 생성자.
     *
     * @param actNo      액션 식별자
     * @param success    실행 성공 여부
     * @param actType    액션 유형
     * @param message    실행 결과 메시지
     * @param output     액션 실행 결과 값
     * @param executedAt 액션 실행 시간 (현재 시간)
     */
    private ActionResult(Long actNo, boolean success, String actType, String message, Object output, LocalDateTime executedAt) {
        this.actNo = actNo;
        this.success = success;
        this.actType = actType;
        this.message = message;
        this.output = output;
        this.executedAt = executedAt;
    }

    /**
     * 액션 실행 결과 객체를 생성하는 정적 팩토리 메서드입니다.
     *
     * @param actNo    액션 식별자
     * @param success  실행 성공 여부
     * @param actType  액션 유형
     * @param message  실행 결과 메시지
     * @param output   액션 실행 결과 값
     * @return         새로 생성된 ActionResult 객체 (실행 시각은 호출 시점의 현재 시간)
     */
    public static ActionResult ofNewActionResult(Long actNo, boolean success, String actType, String message, Object output) {
        return new ActionResult(
                actNo,
                success,
                actType,
                message,
                output,
                LocalDateTime.now()
        );
    }

    public Long getActNo() {
        return actNo;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getActType() {
        return actType;
    }

    public String getMessage() {
        return message;
    }

    public Object getOutput() {
        return output;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    @Override
    public String toString() {
        return "ActionResult{" +
                "actNo=" + actNo +
                ", success=" + success +
                ", actType='" + actType + '\'' +
                ", message='" + message + '\'' +
                ", output=" + output +
                ", executedAt=" + executedAt +
                '}';
    }
}
