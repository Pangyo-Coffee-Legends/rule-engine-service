package com.nhnacademy.ruleengineservice.domain.trigger;

import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * TriggerEvent 엔티티는 규칙이 언제 실행될지 결정하는 트리거(이벤트) 정보를 저장합니다.
 * 각 트리거 이벤트는 타입, 파라미터, 생성일시 등의 정보를 포함합니다.
 * <p>
 * 데이터 생성, 데이터 편집, 항목 추가 등의 이벤트가 규칙을 트리거 합니다.
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
     * 연관된 규칙 엔티티
     * <p>
     * 다대일(Many-to-One) 관계로, {@link Rule} 엔티티를 참조합니다.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_no", referencedColumnName = "rule_no")
    private Rule rule;

    /**
     * 트리거 이벤트의 타입
     * <p>
     * 데이터 베이스 사용 예시)
     * DB_INSERT: 데이터가 삽입될 때,
     * DB_UPDATE: 데이터가 수정될 때,
     * DB_DELETE: 데이터가 삭제될 때
     * <p>
     * 장치/하드웨어 예시)
     * DEVICE_ON: 장치 켜짐,
     * DEVICE_TEMPERATURE_CHANGE: 온도 변화,
     * WEBHOOK: 외부 시스템에서 HTTP 요청이 들어올 때
     * <p>
     * 외부 연동 / 알림 사용 예시)
     * API_CALL: 외부 API 호출,
     * SLACK_MESSAGE: 슬랙 메시지 수신,
     * PUSH_NOTIFICATION: 푸시 알림 수신,
     * WEBHOOK_RECEIVED: 웹훅 수신
     * <p>
     * 스케줄링 예시)
     * SCHEDULE_DAILY: 매일 실행,
     * SCHEDULE_HOURLY: 매시간 실행
     * <p>
     * 사용자 액션 예시)
     * USER_LOGIN: 사용자 로그인
     */
    @Column(nullable = false)
    private String eventType;

    /**
     * 이벤트 파라미터 (JSON 형식 문자열 권장)
     * <p>
     * 예시: {@code "{\"apiEndpoint\": \"/user/login\", \"method\": \"POST\"}"}
     * </p>
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

    /**
     * 트리거 이벤트 생성자
     *
     * @param rule        연관 규칙 엔티티
     * @param eventType   이벤트 유형 (최대 50자)
     * @param eventParams 이벤트 파라미터 (JSON 문자열 권장)
     */
    private TriggerEvent(Rule rule, String eventType, String eventParams) {
        this.rule = rule;
        this.eventType = eventType;
        this.eventParams = eventParams;
    }

    /**
     * 새로운 트리거 이벤트 생성 팩토리 메서드
     *
     * @param rule        연관 규칙 엔티티
     * @param eventType   이벤트 유형
     * @param eventParams 이벤트 파라미터
     * @return 새로 생성된 TriggerEvent 인스턴스
     */
    public static TriggerEvent ofNewTriggerEvent(Rule rule, String eventType, String eventParams) {
        return new TriggerEvent(rule, eventType, eventParams);
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
                ", eventType='" + eventType + '\'' +
                ", eventParams='" + eventParams + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
