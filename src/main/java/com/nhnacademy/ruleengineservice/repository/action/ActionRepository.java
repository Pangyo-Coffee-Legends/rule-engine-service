package com.nhnacademy.ruleengineservice.repository.action;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Action 엔티티의 데이터 접근을 담당하는 레포지토리 인터페이스입니다.
 */
public interface ActionRepository extends JpaRepository<Action, Long> {

    /**
     * 특정 Rule 과 관련된 모든 Action 을 조회합니다.
     *
     * @param rule 조회할 Rule 엔티티
     * @return 해당 Rule 에 속한 Action 리스트
     */
    List<Action> findByRule(Rule rule);

    /**
     * 조건 타입(actType) 으로 Action을 조회합니다.
     *
     * @param actType 조건 타입
     * @return 해당 타입의 Action 리스트
     */
    List<Action> findByActType(String actType);

    /**
     * JSON 파라미터 부분 검색 합니다.
     *
     * @param actParams 검색할 JSON 내용
     * @return 해당하는 JSON 내용이 있는 Action 리스트
     */
    @Query("SELECT a FROM Action a WHERE a.actParams LIKE %:actParams%")
    List<Action> findByActParamsContaining(String actParams);
}
