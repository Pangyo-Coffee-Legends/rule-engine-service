package com.nhnacademy.ruleengineservice.dto.rule;

import java.util.List;

/**
 * 규칙(Rule)의 정보를 포함하는 응답 클래스입니다.
 * 이 클래스는 규칙의 기본 속성과 함께 관련된 액션, 조건, 파라미터, 스케줄, 트리거 이벤트의 ID 목록을 포함합니다.
 * 클라이언트에 Rule 엔티티의 데이터를 전송하는 DTO(Data Transfer Object) 역할을 합니다.
 */
public class RuleResponse {

    /**
     * 규칙의 고유 식별자입니다.
     * 데이터베이스에서 rules 테이블의 rule_no 컬럼 값과 매핑됩니다.
     */
    private Long ruleNo;

    /**
     * 규칙의 이름입니다.
     * 규칙을 식별하는 용도로 사용되며, null 이 될 수 없습니다.
     */
    private String ruleName;

    /**
     * 규칙에 대한 상세 설명입니다.
     * 규칙의 목적과 동작 방식에 대한 설명을 포함합니다.
     */
    private String ruleDescription;

    /**
     * 규칙의 우선순위입니다.
     * 여러 규칙이 동시에 적용될 수 있을 때 우선순위에 따라 적용 순서가 결정됩니다.
     * 숫자가 낮을수록 높은 우선순위를 가집니다.
     */
    private Integer rulePriority;

    /**
     * 규칙의 활성화 상태를 나타냅니다.
     * true 인 경우 규칙이 활성화되어 평가 및 실행 대상이 됩니다.
     * false 인 경우 규칙이 비활성화되어 평가 및 실행 대상에서 제외됩니다.
     */
    private boolean active;

    /**
     * 규칙이 소속된 규칙 그룹의 식별자입니다.
     * 데이터베이스에서 rule_groups 테이블의 rule_group_no 컬럼 값과 매핑됩니다.
     */
    private Long ruleGroupNo;

    /**
     * 이 규칙에 연결된 액션 목록의 식별자입니다.
     * 데이터베이스에서 actions 테이블의 act_no 컬럼 값 목록과 매핑됩니다.
     */
    private List<Long> actionListNo;

    /**
     * 이 규칙에 연결된 조건 목록의 식별자입니다.
     * 데이터베이스에서 conditions 테이블의 con_no 컬럼 값 목록과 매핑됩니다.
     */
    private List<Long> conditionListNo;

    /**
     * 이 규칙에 연결된 파라미터 목록의 식별자입니다.
     * 데이터베이스에서 rule_parameters 테이블의 param_no 컬럼 값 목록과 매핑됩니다.
     */
    private List<Long> ruleParameterListNo;

    /**
     * 이 규칙에 연결된 스케줄 목록의 식별자입니다.
     * 데이터베이스에서 rule_schedules 테이블의 schedule_no 컬럼 값 목록과 매핑됩니다.
     */
    private List<Long> ruleScheduleListNo;

    /**
     * 이 규칙에 연결된 트리거 이벤트 목록의 식별자입니다.
     * 데이터베이스에서 trigger_events 테이블의 trigger_no 컬럼 값 목록과 매핑됩니다.
     */
    private List<Long> triggerEventListNo;

    /**
     * 기본 생성자입니다.
     * JPA 및 Jackson 라이브러리 등에서 사용됩니다.
     */
    protected RuleResponse() {}

    /**
     * 모든 필드를 초기화하는 생성자입니다.
     *
     * @param ruleNo              규칙 식별자
     * @param ruleName            규칙 이름
     * @param ruleDescription     규칙 설명
     * @param rulePriority        규칙 우선순위
     * @param active              규칙 활성화 상태
     * @param ruleGroupNo         규칙 그룹 식별자
     * @param actionListNo        연결된 액션 식별자 목록
     * @param conditionListNo     연결된 조건 식별자 목록
     * @param ruleParameterListNo 연결된 파라미터 식별자 목록
     * @param ruleScheduleListNo  연결된 스케줄 식별자 목록
     * @param triggerEventListNo  연결된 트리거 이벤트 식별자 목록
     */
    public RuleResponse(
            Long ruleNo,
            String ruleName,
            String ruleDescription,
            Integer rulePriority,
            boolean active,
            Long ruleGroupNo,
            List<Long> actionListNo,
            List<Long> conditionListNo,
            List<Long> ruleParameterListNo,
            List<Long> ruleScheduleListNo,
            List<Long> triggerEventListNo
    ) {
        this.ruleNo = ruleNo;
        this.ruleName = ruleName;
        this.ruleDescription = ruleDescription;
        this.rulePriority = rulePriority;
        this.active = active;
        this.ruleGroupNo = ruleGroupNo;
        this.actionListNo = actionListNo;
        this.conditionListNo = conditionListNo;
        this.ruleParameterListNo = ruleParameterListNo;
        this.ruleScheduleListNo = ruleScheduleListNo;
        this.triggerEventListNo = triggerEventListNo;
    }

    public Long getRuleNo() {
        return ruleNo;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public Integer getRulePriority() {
        return rulePriority;
    }

    public boolean isActive() {
        return active;
    }

    public Long getRuleGroupNo() {
        return ruleGroupNo;
    }

    public List<Long> getActionListNo() {
        return actionListNo;
    }

    public List<Long> getConditionListNo() {
        return conditionListNo;
    }

    public List<Long> getRuleParameterListNo() {
        return ruleParameterListNo;
    }

    public List<Long> getRuleScheduleListNo() {
        return ruleScheduleListNo;
    }

    public List<Long> getTriggerEventListNo() {
        return triggerEventListNo;
    }

    @Override
    public String toString() {
        return "RuleResponse{" +
                "ruleNo=" + ruleNo +
                ", ruleName='" + ruleName + '\'' +
                ", ruleDescription='" + ruleDescription + '\'' +
                ", rulePriority=" + rulePriority +
                ", active=" + active +
                ", ruleGroupNo=" + ruleGroupNo +
                ", actionListNo=" + actionListNo +
                ", conditionListNo=" + conditionListNo +
                ", ruleParameterListNo=" + ruleParameterListNo +
                ", ruleScheduleListNo=" + ruleScheduleListNo +
                ", triggerEventListNo=" + triggerEventListNo +
                '}';
    }
}
