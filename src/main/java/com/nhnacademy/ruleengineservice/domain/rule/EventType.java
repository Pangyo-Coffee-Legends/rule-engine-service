package com.nhnacademy.ruleengineservice.domain.rule;

/**
 * 트리거 이벤트의 유형을 정의하는 열거형(enum)입니다.
 * <p>
 * 각 상수는 시스템에서 발생할 수 있는 주요 트리거 이벤트의 종류를 나타냅니다.
 * 이벤트 유형은 코드 내에서 타입 안전하게 사용하며,
 * 각 이벤트에 대한 한글 설명을 함께 제공합니다.
 * </p>
 *
 * <ul>
 *   <li>{@link #DB_INSERT} : 데이터베이스에 데이터가 삽입될 때 발생</li>
 *   <li>{@link #DB_UPDATE} : 데이터베이스의 데이터가 수정될 때 발생</li>
 *   <li>{@link #DB_DELETE} : 데이터베이스의 데이터가 삭제될 때 발생</li>
 *   <li>{@link #API_CALL} : 외부 API 호출이 발생할 때</li>
 *   <li>{@link #WEBHOOK} : 외부 시스템에서 웹훅(Webhook) 이벤트가 수신될 때</li>
 *   <li>{@link #DEVICE_STATUS_CHANGE} : 장치(센서 등)의 상태가 변화할 때</li>
 *   <li>{@link #SCHEDULED_DAILY} : 매일 정해진 시간에 스케줄링된 이벤트</li>
 *   <li>{@link #USER_LOGIN} : 사용자가 로그인할 때</li>
 * </ul>
 *
 * <p>
 * 각 상수는 한글 설명을 포함하고 있으며, {@link #getValue()} 메서드로 조회할 수 있습니다.
 * </p>
 *
 * @author 강승우
 */
public enum EventType {
    /**
     * 데이터베이스에 데이터가 삽입될 때 발생
     */
    DB_INSERT("데이터 삽입"),

    /**
     * 데이터베이스의 데이터가 수정될 때 발생
     */
    DB_UPDATE("데이터 수정"),

    /**
     * 데이터베이스의 데이터가 삭제될 때 발생
     */
    DB_DELETE("데이터 삭제"),

    /**
     * 외부 API 호출이 발생할 때
     */
    API_CALL("외부 API 호출"),

    /**
     * 외부 시스템에서 웹훅(Webhook) 이벤트가 수신될 때
     */
    WEBHOOK("웹훅 수신"),

    /**
     * 장치(센서 등)의 상태가 변화할 때
     */
    DEVICE_STATUS_CHANGE("장치 상태 변화"),

    /**
     * 매일 정해진 시간에 스케줄링된 이벤트
     */
    SCHEDULED_DAILY("매일 실행"),

    /**
     * 사용자가 로그인할 때
     */
    USER_LOGIN("사용자 로그인");

    private final String value;

    /**
     * Event Type 생성자
     * @param value 한글 문자
     */
    EventType(String value) {
        this.value = value;
    }

    /**
     * 이벤트 유형의 한글 설명을 반환합니다.
     *
     * @return 한글 설명
     */
    public String getValue() {
        return value;
    }
}
