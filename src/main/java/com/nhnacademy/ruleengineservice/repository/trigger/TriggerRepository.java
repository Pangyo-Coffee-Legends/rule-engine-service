package com.nhnacademy.ruleengineservice.repository.trigger;

import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.domain.trigger.TriggerEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * TriggerEvent 엔티티의 데이터 액세스를 담당하는 JPA 리포지토리입니다.
 * <p>
 * 트리거 이벤트의 생성, 조회, 수정, 삭제 및 다양한 조건 검색 기능을 제공합니다.
 * </p>
 *
 * @author 강승우
 */
public interface TriggerRepository extends JpaRepository<TriggerEvent, Long> {

    /**
     * 특정 규칙(Rule)에 속한 모든 트리거 이벤트를 조회합니다.
     *
     * @param rule 조회할 Rule 엔티티
     * @return 해당 Rule에 연관된 TriggerEvent 리스트
     */
    List<TriggerEvent> findByRule(Rule rule);

    /**
     * 이벤트 유형(eventType)으로 트리거 이벤트를 조회합니다.
     *
     * @param eventType 이벤트 유형 (예: "DB_INSERT", "API_CALL" 등)
     * @return 해당 이벤트 유형의 TriggerEvent 리스트
     */
    List<TriggerEvent> findByEventType(String eventType);

    /**
     * 이벤트 파라미터(eventParams)에 특정 문자열이 포함된 트리거 이벤트를 조회합니다.
     * <p>
     * eventParams는 일반적으로 JSON 문자열로 저장되며,
     * 부분 문자열 검색을 통해 특정 파라미터가 포함된 이벤트를 찾을 때 사용합니다.
     * </p>
     *
     * @param eventParams 검색할 파라미터(부분 문자열)
     * @return 해당 파라미터를 포함하는 TriggerEvent 리스트
     */
    @Query("SELECT t FROM TriggerEvent t WHERE t.eventParams LIKE %:eventParams%")
    List<TriggerEvent> findByEventParams(String eventParams);
}
