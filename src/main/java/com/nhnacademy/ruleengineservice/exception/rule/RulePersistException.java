package com.nhnacademy.ruleengineservice.exception.rule;

/**
 * 룰(Rule) 엔티티의 저장, 수정, 삭제 등 영속화(persistence) 작업 중 오류가 발생했을 때 throw되는 예외입니다.
 * <p>
 * 이 예외는 데이터베이스에 룰을 저장하거나 갱신, 삭제하는 과정에서
 * 예상치 못한 오류가 발생한 경우 사용됩니다.
 * </p>
 *
 * <pre>
 * 예시:
 * try {
 *     ruleRepository.save(rule);
 * } catch (DataAccessException e) {
 *     throw new RulePersistException("룰 저장에 실패했습니다: " + e.getMessage());
 * }
 * </pre>
 *
 * @author 강승우
 */
public class RulePersistException extends RuntimeException {
    /**
     * 상세 메시지를 포함하는 RulePersistException을 생성합니다.
     *
     * @param message 예외의 상세 메시지
     */
    public RulePersistException(String message) {
        super(message);
    }
}
