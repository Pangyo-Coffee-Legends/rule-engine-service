package com.nhnacademy.ruleengineservice.domain.condition;

import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Condition 엔티티는 규칙 평가에 사용되는 개별 조건을 나타냅니다.
 * 각 Condition은 비교 타입, 비교할 필드, 비교 값, 우선순위 등으로 구성됩니다.
 */
@Entity
public class Condition {

    /**
     * 조건의 고유 식별자(PK).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "con_no")
    private Long conditionNo;

    /**
     * 다 대 일 관계로 매핑
     * 연관관계의 주인은 PK를 가지고 있는 쪽
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_no", referencedColumnName = "rule_no")
    private Rule rule;

    /**
     * 조건 비교 타입
     * <p>
     * EQ: 값이 같은지 비교,
     * NE: 값이 다른지 비교,
     * GT: 값이 큰지 비교,
     * LT: 값이 작은지 비교,
     * GTE: 크거나 같은지 비교,
     * LTE: 작거나 같은지 비교,
     * IN: 포함,
     * NOT_IN: 미포함,
     * LIKE: 패턴 매칭,
     * NOT_LIKE: 패턴 불일치,
     * BETWEEN: 구간 포함,
     * IS_NULL: 값이 없음,
     * IS_NOT_NULL: 값이 있음
     */
    @Column(nullable = false, length = 50)
    private String conType;

    /**
     * 조건이 적용될 필드명.
     */
    @Column(nullable = false, length = 50)
    private String conField;

    /**
     * 비교할 값.
     */
    @Column(nullable = false, length = 100)
    private String conValue;

    /**
     * 조건 평가 우선순위 (낮을수록 먼저 평가됨).
     */
    @Column(nullable = false)
    private Integer conPriority;

    /**
     * 조건 생성 일시.
     */
    private LocalDateTime createdAt;

    /**
     * JPA 기본 생성자 (외부에서 직접 호출하지 않음).
     */
    protected Condition() {}

    /**
     * Condition 객체의 생성자.
     *
     * @param conType     조건 비교 타입 (예: EQ, GT, LT 등)
     * @param conField    조건이 적용될 필드명
     * @param conValue    비교할 값
     * @param conPriority 조건 평가 우선순위
     */
    private Condition(String conType, String conField, String conValue, Integer conPriority) {
        this.conType = conType;
        this.conField = conField;
        this.conValue = conValue;
        this.conPriority = conPriority;
    }

    /**
     * Condition 객체를 생성하는 정적 팩토리 메서드.
     *
     * @param conType     조건 비교 타입 (예: EQ, GT, LT 등)
     * @param conField    조건이 적용될 필드명
     * @param conValue    비교할 값
     * @param conPriority 조건 평가 우선순위
     * @return 새 Condition 인스턴스
     */
    public static Condition ofNewCondition(String conType, String conField, String conValue, Integer conPriority) {
        return new Condition(conType, conField, conValue, conPriority);
    }

    /**
     * 생성시 자동으로 생성 날짜를 만들어 준다.
     */
    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now(); }

    public Rule getRule() {
        return rule;
    }

    public Long getConditionNo() {
        return conditionNo;
    }

    public String getConType() {
        return conType;
    }

    public String getConField() {
        return conField;
    }

    public String getConValue() {
        return conValue;
    }

    public Integer getConPriority() {
        return conPriority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Condition{" +
                "conditionNo=" + conditionNo +
                ", rule=" + rule +
                ", conType='" + conType + '\'' +
                ", conField='" + conField + '\'' +
                ", conValue='" + conValue + '\'' +
                ", conPriority=" + conPriority +
                ", createdAt=" + createdAt +
                '}';
    }
}
