package com.nhnacademy.ruleengineservice.domain.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Action 엔티티는 규칙이 발동될 때 실행될 동작(액션)을 정의합니다.
 * 각 액션은 타입, 파라미터, 우선순위, 생성일시 등의 정보를 포함합니다.
 */
@Entity
public class Action {

    /**
     * 액션의 고유 식별자(PK).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "act_no")
    private Long actNo;

    /**
     * 다 대 일 관계로 매핑
     * 연관관계의 주인은 PK를 가지고 있는 쪽
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_no", referencedColumnName = "rule_no")
    private Rule rule;

    /**
     * 액션의 타입
     * 사용예시)
     * EMAIL: 이메일 발송,
     * SMS: 문자 메시지 발송,
     * PUSH: 모바일 웹/앱 푸시 알림 발송,
     * WEBHOOK: 외부 시스템으로 HTTP 요청 전송,
     * SLACK: 슬랙 메시지 전송,
     * DATABASE: DB에 값 저장/수정/삭제 등 쿼리 실행,
     * API_CALL: 외부 API 호출(HTTP RESTful API 등),
     * FILE_UPLOAD: 파일 업로드(예: FTP, S3, 내부 파일 서버 등),
     * LOG: 시스템 로그 기록,
     * NOTIFICATION: 다양한 내/외부 시스템 알림(예: 사내 알림, ERP 연동 등)
     */
    @Column(nullable = false, length = 50)
    private String actType;

    /**
     * 액션 실행에 필요한 파라미터(JSON 등).
     */
    @Column(nullable = false)
    private String actParams;

    /**
     * 액션 실행 우선순위(낮을수록 먼저 실행).
     */
    @Column(nullable = false)
    private Integer actPriority;

    /**
     * 액션이 생성된 일시.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * JPA 기본 생성자 (외부에서 직접 호출하지 않음).
     */
    protected Action() {}

    /**
     * Action 객체의 생성자.
     *
     * @param rule         연관 규칙 엔티티
     * @param actType      액션 비교 타입 (예: EMAIL, PUSH, API_CALL, LOG 등)
     * @param actParams    행동이 적용될 필드명
     * @param actPriority  행동 평가 우선순위
     */
    private Action(Rule rule, String actType, String actParams, Integer actPriority) {
        this.rule = rule;
        this.actType = actType;
        this.actParams = actParams;
        this.actPriority = actPriority;
    }

    /**
     * Action 객체를 생성하는 정적 팩토리 메서드.
     *
     * @param rule         연관 규칙 엔티티
     * @param actType      액션 비교 타입 (예: EMAIL, PUSH, API_CALL, LOG 등)
     * @param actParams    행동이 적용될 필드명
     * @param actPriority  행동 평가 우선순위
     * @return 새 Action 인스턴스
     */
    public static Action ofNewAction(Rule rule, String actType, String actParams, Integer actPriority) {
        return new Action(rule, actType, actParams, actPriority);
    }

    /**
     * 생성시 자동으로 생성 날짜를 만들어 준다.
     * 저장 전 Map -> JSON 문자열 변환
     */
    @PrePersist
    public void prePersist() throws JsonProcessingException {
        this.createdAt = LocalDateTime.now();

        if (actParamsMap != null) {
            ObjectMapper mapper = new ObjectMapper();
            this.actParams = mapper.writeValueAsString(actParamsMap);
        }
    }

    /**
     * DB 에 저장되지 않는 임시 필드
     */
    @Transient
    private Map<String, Object> actParamsMap;

    /**
     * 조회 후 JSON 문자열 -> Map 변환
     * @throws JsonProcessingException Json 임시 처리
     */
    @PostLoad
    public void postLoad() throws JsonProcessingException {
        if (actParams != null) {
            ObjectMapper mapper = new ObjectMapper();
            this.actParamsMap = mapper.readValue(actParams, new TypeReference<Map<String, Object>>() {
            });
        }
    }

    public Map<String, Object> getActParamsMap() {
        return actParamsMap;
    }

    public void setActParamsMap(Map<String, Object> actParamsMap) {
        this.actParamsMap = actParamsMap;
    }

    public Rule getRule() {
        return rule;
    }

    public Long getActNo() {
        return actNo;
    }

    public String getActType() {
        return actType;
    }

    public String getActParams() {
        return actParams;
    }

    public Integer getActPriority() {
        return actPriority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Action{" +
                "actNo=" + actNo +
                ", actType='" + actType + '\'' +
                ", actParams='" + actParams + '\'' +
                ", actPriority=" + actPriority +
                ", createdAt=" + createdAt +
                '}';
    }
}
