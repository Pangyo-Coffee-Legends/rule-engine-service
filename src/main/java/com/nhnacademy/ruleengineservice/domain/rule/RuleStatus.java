package com.nhnacademy.ruleengineservice.domain.rule;

/**
 * RuleStatus는 규칙(Rule)의 상태를 나타내는 열거형(Enum)입니다.
 * 활성(ENABLED), 비활성(DISABLED), 대기(PENDING), 삭제됨(DELETED)
 * <p>
 * 필요에 따라 상태와 한글 이름(설명)을 함께 가질 수 있습니다.
 * 현재 active 대신 이걸 사용하여 확장성에 대해 고민 중 입니다.
 *
 * @author 강승우
 */
public enum RuleStatus {
    ENABLED("활성"),
    DISABLED("비활성"),
    PENDING("대기"),
    DELETE("삭제");

    private final String name;

    RuleStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
