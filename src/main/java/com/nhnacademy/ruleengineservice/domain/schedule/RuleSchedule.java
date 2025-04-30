package com.nhnacademy.ruleengineservice.domain.schedule;

import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * RuleSchedule 엔티티는 규칙의 실행 일정을 정의합니다.
 * 크론 표현식, 시간대, 최대 재시도 횟수, 활성화 여부, 생성일시 등을 관리합니다.
 */
@Entity
@Table(name = "rule_schedules")
public class RuleSchedule {

    /**
     * 스케줄의 고유 식별자(PK). 자동 증가 방식으로 생성됩니다.
     */
    @Id
    @GeneratedValue
    @Column(name = "schedule_no")
    private Long scheduleNo;

    /**
     * 다 대 일 관계로 매핑
     * 연관관계의 주인은 PK를 가지고 있는 쪽
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_no", referencedColumnName = "rule_no")
    private Rule rule;

    /**
     * 크론 표현식. 작업이 언제 실행될지 정의하는 문자열입니다.
     * 6~7개의 필드(초, 분, 시, 일, 월, 요일, [연도])로 구성되고
     * 각 필드는 공백으로 구분합니다.
     * <p>
     * 사용예시)
     * "*" : 모든 값(매번),
     * "," : 여러 값(1,3,5),
     * "-" : 범위(1-5),
     * "/" : 주기(0/5 → 5마다),
     * "?" : 특정 필드 무시(일/요일 중 하나),
     * "L" : 마지막(월의 마지막 날 등),
     * "W" : 가장 가까운 평일,
     * "#" : n번째 요일(예: 5#3 → 셋째 금요일),
     * "R" : 무작위
     * <p>
     * 실제사용)
     * "0 0 7 * * ?": 매일 오전 7시,
     * "0 0 12 * * ? *": 매일 12:00(정오)에 실행,
     * "0 15 10 ? * MON-FRI *": 월~금 오전 10:15에 실행,
     * "
     */
    @Column(nullable = false, length = 50)
    private String cronExpression;

    /**
     * 시간대(IANA Time Zone ID). 스케줄이 적용될 표준 시간대입니다.
     * <p>
     * 사용예시)
     * "Asia/Seoul",
     * "UTC",
     * "America/New_York"
     */
    @Column(nullable = false, length = 50)
    private String timeZone;

    /**
     * 최대 재시도 횟수. 스케줄 실행 실패 시 재시도 가능한 최대 횟수입니다.
     */
    @Column(nullable = false)
    private Integer maxRetires;

    /**
     * 스케줄 활성화 여부. true 면 활성, false 면 비활성 상태입니다.
     */
    @Column(nullable = false)
    private Boolean active;

    /**
     * 스케줄이 생성된 일시.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * JPA 기본 생성자 (외부에서 직접 호출하지 않음).
     */
    protected RuleSchedule() {}

    /**
     * RuleSchedule 객체의 생성자
     *
     * @param rule              연관 규칙 엔티티
     * @param cronExpression    크론 표현식
     * @param timeZone          시간대
     * @param maxRetires        최대 재시도 횟수
     * @param active            활성화 여부
     */
    private RuleSchedule(Rule rule, String cronExpression, String timeZone, Integer maxRetires, Boolean active) {
        this.rule = rule;
        this.cronExpression = cronExpression;
        this.timeZone = timeZone;
        this.maxRetires = maxRetires;
        this.active = active;
    }

    /**
     * RuleSchedule 객체의 생성자
     *
     * @param rule              연관 규칙 엔티티
     * @param cronExpression    크론 표현식
     * @param timeZone          시간대
     * @param maxRetires        최대 재시도 횟수
     * @return 새 RuleSchedule 인스턴스
     */
    public static RuleSchedule ofNewRuleSchedule(Rule rule, String cronExpression, String timeZone, Integer maxRetires) {
        return new RuleSchedule(rule, cronExpression, timeZone, maxRetires, true);
    }

    /**
     * 생성시 자동으로 생성 날짜를 만들어 준다.
     */
    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now(); }

    public Rule getRule() {
        return rule;
    }

    public Long getScheduleNo() {
        return scheduleNo;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public Integer getMaxRetires() {
        return maxRetires;
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "RuleSchedule{" +
                "scheduleNo=" + scheduleNo +
                ", cronExpression='" + cronExpression + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", maxRetires=" + maxRetires +
                ", active=" + active +
                ", createdAt=" + createdAt +
                '}';
    }
}
