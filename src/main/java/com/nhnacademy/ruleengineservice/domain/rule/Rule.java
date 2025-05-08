package com.nhnacademy.ruleengineservice.domain.rule;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.domain.condition.Condition;
import com.nhnacademy.ruleengineservice.domain.parameter.RuleParameter;
import com.nhnacademy.ruleengineservice.domain.schedule.RuleSchedule;
import com.nhnacademy.ruleengineservice.domain.trigger.TriggerEvent;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 규칙 엔진에서 사용되는 기본 규칙(Rule) 엔티티 클래스입니다.
 * 각 규칙은 고유 식별자, 이름, 설명, 우선순위 및 활성 상태를 가집니다.
 * 이 클래스는 규칙 엔진 서비스의 핵심 도메인 객체로 사용됩니다.
 *
 * @author 강승우
 */
@Entity
@Getter
@Table(name = "rules")
public class Rule {

    /**
     * 규칙의 고유 식별자입니다.
     * 자동 생성되는 ID 값으로, 데이터베이스에서 기본 키로 사용됩니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_no")
    private Long ruleNo;

    /**
     * 규칙의 이름입니다.
     * 규칙을 식별하기 위한 용도로 사용되며, null 이 될 수 없습니다.
     */
    @Column(nullable = false, length = 50)
    private String ruleName;

    /**
     * 규칙에 대한 상세 설명입니다.
     * 규칙의 목적과 동작 방식에 대한 설명을 포함합니다.
     */
    @Column(length = 200)
    private String ruleDescription;

    /**
     * 규칙의 우선순위입니다.
     * 여러 규칙이 동시에 적용될 수 있을 때 우선순위에 따라 적용 순서가 결정됩니다.
     * 숫자가 낮을수록 높은 우선순위를 가집니다.
     */
    @Column(nullable = false)
    private Integer rulePriority;

    /**
     * 규칙의 활성화 상태를 나타냅니다.
     * true 인 경우 규칙이 활성화되어 평가 및 실행 대상이 됩니다.
     * false 인 경우 규칙이 비활성화되어 평가 및 실행 대상에서 제외됩니다.
     */
    private boolean active;

    /**
     * 규칙이 소속된 규칙 그룹입니다.
     * 다대일(N:1) 관계이며, 실제 외래키는 rule_group_no 컬럼에 저장됩니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_group_no", nullable = false)
    private RuleGroup ruleGroup;

    /**
     * 이 규칙에 연결된 액션 목록입니다.
     * 일대다(1:N) 관계이며, 액션이 규칙을 참조합니다.
     */
    @OneToMany(mappedBy = "rule")
    private List<Action> actionList = new ArrayList<>();

    /**
     * 이 규칙에 연결된 조건(Condition) 목록입니다.
     * 일대다(1:N) 관계이며, 조건이 규칙을 참조합니다.
     */
    @OneToMany(mappedBy = "rule")
    private List<Condition> conditionList = new ArrayList<>();

    /**
     * 이 규칙에 연결된 파라미터(RuleParameter) 목록입니다.
     * 일대다(1:N) 관계이며, 파라미터가 규칙을 참조합니다.
     */
    @OneToMany(mappedBy = "rule")
    private List<RuleParameter> ruleParameterList = new ArrayList<>();

    /**
     * 이 규칙에 연결된 스케줄(RuleSchedule) 목록입니다.
     * 일대다(1:N) 관계이며, 스케줄이 규칙을 참조합니다.
     */
    @OneToMany(mappedBy = "rule")
    private List<RuleSchedule> ruleScheduleList = new ArrayList<>();

    /**
     * 이 규칙에 연결된 트리거 이벤트(TriggerEvent) 목록입니다.
     * 일대다(1:N) 관계이며, 트리거 이벤트가 규칙을 참조합니다.
     */
    @OneToMany(mappedBy = "rule")
    private List<TriggerEvent> triggerEventList = new ArrayList<>();

    /**
     * 규칙이 생성된 날짜와 시간입니다.
     */
    private LocalDateTime createdAt;

    /**
     * 규칙이 마지막으로 업데이트된 날짜와 시간입니다.
     */
    private LocalDateTime updatedAt;

    /**
     * JPA 기본 생성자
     */
    protected Rule() {}

    /**
     * 규칙 객체를 생성하는 private 생성자입니다.
     * 직접 생성자를 호출하지 않고 팩토리 메서드를 통해 객체를 생성해야 합니다.
     *
     * @param ruleName 규칙 이름
     * @param ruleDescription 규칙 설명
     * @param rulePriority 규칙 우선순위
     * @param active 규칙 활성화 상태
     */
    private Rule(RuleGroup ruleGroup, String ruleName, String ruleDescription, Integer rulePriority, boolean active) {
        this.ruleGroup = ruleGroup;
        this.ruleName = ruleName;
        this.ruleDescription = ruleDescription;
        this.rulePriority = rulePriority;
        this.active = active;
    }

    /**
     * 새로운 규칙 객체를 생성하는 팩토리 메서드입니다.
     * 기본적으로 활성화 상태(isActive=true)로 생성됩니다.
     *
     * @param ruleName 규칙 이름 (필수)
     * @param ruleDescription 규칙 설명
     * @param rulePriority 규칙 우선순위 (필수)
     * @return 새로 생성된 Rule 객체
     */
    public static Rule ofNewRule(RuleGroup ruleGroup, String ruleName, String ruleDescription, Integer rulePriority) {
        return new Rule(ruleGroup, ruleName, ruleDescription, rulePriority, true);
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
     * 규칙(Rule)의 이름, 설명, 우선순위 값을 수정합니다.
     * <p>
     * 이 메서드는 엔티티의 주요 속성(이름, 설명, 우선순위)을 변경할 때 사용하며,
     * JPA의 변경 감지(dirty checking) 기능에 의해 트랜잭션 커밋 시점에 자동으로 DB에 반영됩니다.
     *
     * @param ruleName        새로 설정할 규칙 이름
     * @param ruleDescription 새로 설정할 규칙 설명
     * @param rulePriority    새로 설정할 규칙 우선순위
     */
    public void ruleUpdate(String ruleName, String ruleDescription, Integer rulePriority) {
        this.ruleName = ruleName;
        this.ruleDescription = ruleDescription;
        this.rulePriority = rulePriority;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setRuleGroup(RuleGroup ruleGroup) {
        this.ruleGroup = ruleGroup;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "ruleNo=" + ruleNo +
                ", ruleName='" + ruleName + '\'' +
                ", ruleDescription='" + ruleDescription + '\'' +
                ", rulePriority=" + rulePriority +
                ", active=" + active +
                ", actionList=" + actionList +
                ", conditionList=" + conditionList +
                ", ruleParameterList=" + ruleParameterList +
                ", ruleScheduleList=" + ruleScheduleList +
                ", triggerEventList=" + triggerEventList +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
