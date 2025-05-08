package com.nhnacademy.ruleengineservice.exception.email;

/**
 * 이메일 파라미터(예: JSON 파싱 등) 처리 중 오류가 발생했을 때 throw되는 예외입니다.
 * <p>
 * 이 예외는 이메일 발송을 위한 파라미터를 파싱하거나 변환하는 과정에서
 * 포맷이 잘못되었거나 필수 값이 누락된 경우 등 파싱 관련 문제가 발생했을 때 사용됩니다.
 * </p>
 *
 * <pre>
 * 예시:
 * try {
 *     JsonNode emailParams = objectMapper.readTree(paramString);
 * } catch (JsonProcessingException e) {
 *     throw new EmailParameterParseException("이메일 파라미터 파싱 실패", e);
 * }
 * </pre>
 *
 * @author 강승우
 */
public class EmailParameterParseException extends RuntimeException {
    /**
     * 상세 메시지와 원인 예외를 포함하는 EmailParameterParseException을 생성합니다.
     *
     * @param message 예외의 상세 메시지
     * @param cause   파싱 실패 등 원인 예외
     */
    public EmailParameterParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
