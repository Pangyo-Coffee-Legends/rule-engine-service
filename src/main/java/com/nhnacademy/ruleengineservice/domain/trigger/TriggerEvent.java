package com.nhnacademy.ruleengineservice.domain.trigger;

import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * TriggerEvent 엔티티는 규칙이 언제 실행될지 결정하는 트리거(이벤트) 정보를 저장합니다.
 * 각 트리거 이벤트는 타입, 파라미터, 생성일시 등의 정보를 포함합니다.
 */
@Entity
public class TriggerEvent {

    /**
     * 트리거 이벤트의 고유 식별자(PK).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_no")
    private Long eventNo;

    /**
     * 다 대 일 관계로 매핑
     * 연관관계의 주인은 PK를 가지고 있는 쪽
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_no", referencedColumnName = "rule_no")
    private Rule rule;

    /**
     * 트리거 이벤트의 타입
     * <p>
     * 데이터 베이스 사용 예시)
     * INSERT: 데이터가 삽입될 때,
     * UPDATE: 데이터가 수정될 때,
     * DELETE: 데이터가 삭제될 때,
     * SELECT: 데이터가 조회될 때(일부 DB에서 지원),
     * SCHEDULE: 정해진 시간(스케줄)에 따라,
     * ON_BEFORE_INSERT / ON_AFTER_INSERT: 삽입 전/후,
     * ON_BEFORE_UPDATE / ON_AFTER_UPDATE: 수정 전/후,
     * ON_BEFORE_DELETE / ON_AFTER_DELETE: 삭제 전/후,
     * ON_BEFORE_VALIDATE / ON_AFTER_VALIDATE: 필드 값 검증 전/후
     * <p>
     * 이벤트 기반 사용 예시)
     * DEVICE_STATUS_CHANGE: 장치 상태 변화(예: 센서 on/off),
     * LOCATION_EVENT: 위치/모드 변경,
     * VARIABLE_CHANGE: 변수 값 변경,
     * WEBHOOK: 외부 시스템에서 HTTP 요청이 들어올 때,
     * TIMER: 특정 시간, 주기, 타이머 만료,
     * SUNRISE/SUNSET: 일출/일몰 등 자연 현상 기반,
     * CUSTOM_EVENT: 사용자 정의 이벤트
     * <p>
     * 외부 연동 / 알림 사용 예시)
     * HTTP_REQUEST: 외부 API 호출,
     * SLACK_MESSAGE: 슬랙 메시지 수신,
     * PUSH_NOTIFICATION: 푸시 알림 수신
     */
    @Column(nullable = false)
    private String eventType;

    /**
     * 트리거 이벤트에 필요한 파라미터(JSON 등).
     */
    @Column(nullable = false)
    private String eventParams;

    /**
     * 트리거 이벤트가 생성된 일시.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * JPA 기본 생성자 (외부에서 직접 호출하지 않음).
     */
    protected TriggerEvent() {}

    private TriggerEvent(String eventType, String eventParams) {
        this.eventType = eventType;
        this.eventParams = eventParams;
    }

    public static TriggerEvent ofNewTriggerEvent(String eventType, String eventParams) {
        return new TriggerEvent(eventType, eventParams);
    }

    /**
     * 생성시 자동으로 생성 날짜를 만들어 준다.
     */
    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now(); }

    public Rule getRule() {
        return rule;
    }

    public Long getEventNo() {
        return eventNo;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventParams() {
        return eventParams;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "TriggerEvent{" +
                "eventNo=" + eventNo +
                ", rule=" + rule +
                ", eventType='" + eventType + '\'' +
                ", eventParams='" + eventParams + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
