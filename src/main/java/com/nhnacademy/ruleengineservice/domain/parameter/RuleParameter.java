package com.nhnacademy.ruleengineservice.domain.parameter;

import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * RuleParameter 엔티티는 규칙(Rule)에 연결된 파라미터(매개변수) 정보를 저장합니다.
 * 각 파라미터는 규칙 실행 시 필요한 동적 값을 관리하는 데 사용됩니다.
 */
@Entity
@Table(name = "rule_parameters")
public class RuleParameter {

    /**
     * 파라미터의 고유 식별자(PK). 자동 증가 방식으로 생성됩니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "param_no")
    private Long paramNo;

    /**
     * 파라미터 이름. 최대 50자까지 저장 가능하며, NULL을 허용하지 않습니다.
     * 예: "max_limit", "retry_count"
     */
    @Column(nullable = false, length = 50)
    private String paramName;

    /**
     * 다 대 일 관계로 매핑
     * 연관관계의 주인은 PK를 가지고 있는 쪽
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_no", referencedColumnName = "rule_no")
    private Rule rule;

    /**
     * 파라미터 값. 최대 100자까지 저장 가능하며, NULL을 허용하지 않습니다.
     * 예: "100000", "3"
     */
    @Column(nullable = false, length = 100)
    private String paramValue;

    /**
     * 파라미터가 생성된 일시. NULL을 허용하지 않습니다.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * 파라미터가 마지막으로 수정된 일시. NULL 허용 (생성 후 수정되지 않았을 경우 NULL).
     */
    private LocalDateTime updatedAt;

    /**
     * JPA 기본 생성자 (외부에서 직접 호출하지 않음).
     */
    protected RuleParameter() {}

    /**
     * Rule Parameter 객체의 생성자
     *
     * @param paramName 파라미터 이름
     * @param paramValue 파라미터 값
     */
    private RuleParameter(String paramName, String paramValue) {
        this.paramName = paramName;
        this.paramValue = paramValue;
    }

    /**
     * Rule Parameter 객체를 생성하는 정적 팩토리 메서드
     *
     * @param paramName 파라미터 이름
     * @param paramValue 파라미터 값
     * @return 새 Rule Parameter 인스턴스
     */
    public static RuleParameter ofNewRuleParameter(String paramName, String paramValue) {
        return new RuleParameter(paramName, paramValue);
    }

    /**
     * 생성시 자동으로 생성 날짜를 만들어 준다.
     */
    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now(); }

    /**
     * 업데이트시 자동으로 업데이트 날짜를 만들어 준다.
     */
    @PreUpdate
    public void preUpdate() { this.updatedAt = LocalDateTime.now(); }

    public Long getParamNo() {
        return paramNo;
    }

    public String getParamName() {
        return paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "RuleParameter{" +
                "paramNo=" + paramNo +
                ", paramName='" + paramName + '\'' +
                ", paramValue='" + paramValue + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}