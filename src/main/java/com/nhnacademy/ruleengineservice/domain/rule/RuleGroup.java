package com.nhnacademy.ruleengineservice.domain.rule;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * RuleGroup 엔티티는 여러 Rule(규칙)을 하나의 그룹으로 묶어 관리하는 도메인 객체입니다.
 * 그룹 단위로 이름, 설명, 활성화 여부, 우선순위 등의 속성을 가지며,
 * 그룹에 속한 Rule 목록을 포함합니다.
 *
 * <p>
 * 주요 필드:
 * <ul>
 *     <li>ruleGroupId: 그룹 고유 식별자</li>
 *     <li>ruleGroupName: 그룹 이름 (유니크)</li>
 *     <li>ruleGroupDescription: 그룹 설명</li>
 *     <li>active: 그룹 활성화 여부</li>
 *     <li>priority: 그룹 우선순위</li>
 *     <li>rules: 그룹에 포함된 규칙 목록</li>
 *     <li>createdAt: 생성일자</li>
 *     <li>updatedAt: 수정일자</li>
 * </ul>
 *
 * <p>
 * JPA 라이프사이클 콜백을 통해 생성/수정 시간을 자동으로 관리합니다.
 *
 * @author 강승우
 */
@Entity
@Table(name = "rule_groups")
public class RuleGroup {
    /**
     * 그룹의 고유 식별자입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_group_no")
    private Long ruleGroupNo;

    /**
     * 그룹의 이름입니다. (유니크)
     */
    @Column(nullable = false, unique = true, length = 50)
    private String ruleGroupName;

    /**
     * 그룹에 대한 설명입니다.
     */
    @Column(length = 200)
    private String ruleGroupDescription;

    /**
     * 그룹의 우선순위입니다. 숫자가 낮을수록 우선순위가 높습니다.
     */
    @Column(nullable = false)
    private Integer priority;

    /**
     * 그룹의 활성화 상태입니다.
     */
    @Column(nullable = false)
    private boolean active;

    /**
     * 그룹에 속한 규칙(Rule)들의 목록입니다.
     * JPA 가 Rule 테이블에 rule_group_id 라는 외래키를 만들어 관리합니다.
     */
    @OneToMany(
            mappedBy = "ruleGroup", // 연관관계 주인이 아닌 객체
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Rule> ruleList = new ArrayList<>();

    /**
     * 그룹 생성 시각입니다.
     */
    private LocalDateTime createdAt;

    /**
     * 그룹 수정 시각입니다.
     */
    private LocalDateTime updatedAt;

    /**
     * JPA 기본 생성자입니다.
     */
    protected RuleGroup() {}

    /**
     * 규칙 객체를 생성하는 private 생성자입니다.
     * 직접 생성자를 호출하지 않고 팩토리 메서드를 통해 객체를 생성해야 합니다.
     *
     * @param ruleGroupName 그룹 이름
     * @param ruleGroupDescription 그룹 설명
     * @param priority 우선순위
     * @param active 활성화 여부
     */
    private RuleGroup(String ruleGroupName, String ruleGroupDescription, Integer priority, boolean active) {
        this.ruleGroupName = ruleGroupName;
        this.ruleGroupDescription = ruleGroupDescription;
        this.active = active;
        this.priority = priority;
    }

    /**
     * 새로운 규칙 객체를 생성하는 팩토리 메서드입니다.
     * 기본적으로 활성화 상태로 생성됩니다.
     *
     * @param ruleGroupName 그룹 이름
     * @param ruleGroupDescription 그룹 설명
     * @param priority 우선순위
     * @return 새로 생성된 Rule Group 객체
     */
    public static RuleGroup ofNewRuleGroup(String ruleGroupName, String ruleGroupDescription, Integer priority) {
        return new RuleGroup(ruleGroupName,ruleGroupDescription, priority,true);
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

    /**
     * Rule 을 RuleGroup에 추가한다
     *
     * @param rule 추가할 rule
     */
    public void addRule(Rule rule) {
        this.ruleList.add(rule);
        rule.setRuleGroup(this);
    }

    /**
     * Rule 을 RuleGroup에서 제거한다.
     *
     * @param rule 제거할 rule
     */
    public void removeRule(Rule rule) {
        this.ruleList.remove(rule);
        rule.setRuleGroup(null);
    }

    /**
     * RuleGroup 안에 있는 Rule 을 모두 비운다.
     */
    public void clearRules() {
        this.ruleList.clear();
    }

    public Long getRuleGroupNo() {
        return ruleGroupNo;
    }

    public String getRuleGroupName() {
        return ruleGroupName;
    }

    public String getRuleGroupDescription() {
        return ruleGroupDescription;
    }

    public boolean isActive() {
        return active;
    }

    public Integer getPriority() {
        return priority;
    }

    public List<Rule> getRuleList() {
        return ruleList;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "RuleGroup{" +
                "ruleGroupNo=" + ruleGroupNo +
                ", ruleGroupName='" + ruleGroupName + '\'' +
                ", ruleGroupDescription='" + ruleGroupDescription + '\'' +
                ", active=" + active +
                ", priority=" + priority +
                ", ruleList=" + ruleList +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
