package com.nhnacademy.ruleengineservice.service.condition;

import com.nhnacademy.ruleengineservice.domain.condition.Condition;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.dto.condition.ConditionRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.condition.ConditionResponse;
import com.nhnacademy.ruleengineservice.dto.condition.ConditionResult;

import java.util.List;
import java.util.Map;

/**
 * 조건(Condition) 도메인 객체의 비즈니스 로직을 담당하는 서비스 인터페이스입니다.
 * <p>
 * 이 인터페이스는 conditions 테이블의 CRUD 연산, 특정 룰에 연결된 조건 관리,
 * 조건 평가(EQ, GT, LT, LIKE 등) 및 조건 평가 결과 반환 등 핵심 비즈니스 기능을 정의합니다.
 * </p>
 * <p>
 * 주요 역할:
 * <ul>
 *   <li>조건 등록, 수정, 삭제, 단건/목록 조회</li>
 *   <li>특정 룰에 연결된 조건 목록 조회 및 필수 필드 추출</li>
 *   <li>조건 평가(evaluateCondition), 조건 평가 결과(ConditionResult) 반환</li>
 *   <li>룰 엔진과의 연동(조건 일괄 평가, 필수 데이터 검증 등)</li>
 * </ul>
 * </p>
 *
 * @author 강승우
 */
public interface ConditionService {

    /**
     * 새로운 조건을 등록합니다.
     * @param request 조건 등록 DTO
     * @return 생성된 Condition의 ID
     */
    ConditionResponse registerCondition(ConditionRegisterRequest request);

    /**
     * 조건을 삭제합니다.
     * @param conditionNo 조건 식별자
     */
    void deleteCondition(Long conditionNo);

    /**
     * 조건 단건 조회
     * @param conditionNo 조건 식별자
     * @return Condition 상세 DTO
     */
    ConditionResponse getCondition(Long conditionNo);

    /**
     * 특정 룰에 속한 조건 목록 조회
     * @param ruleNo 룰 식별자
     * @return 조건 응답 DTO 리스트
     */
    List<ConditionResponse> getConditionsByRule(Long ruleNo);

    /**
     * 입력값에 대해 조건을 평가합니다.
     * @param conditionNo 조건 식별자
     * @param facts 평가에 사용할 데이터(파라미터)
     * @return 조건 만족 여부
     */
    boolean evaluateCondition(Long conditionNo, Map<String, Object> facts);

    /**
     * Condition Repository 에 있는 내용을 공통 처리하기 위한 메서드
     *
     * @param conditionNo condition 식별자
     * @return 조건 식별자
     */
    Condition getConditionEntity(Long conditionNo);

    /**
     * 특정 룰에 필요한 필수 필드 목록을 가져옵니다.
     * <p>
     * 이 메서드는 룰에 연결된 조건들을 분석하여 필요한 데이터 필드들을 식별합니다.
     * 룰 엔진에서 데이터의 유효성을 검증하기 전에 필요한 모든 필드가 존재하는지 확인하는 데 사용됩니다.
     * </p>
     *
     * @param rule 필수 필드를 확인할 룰 객체
     * @return 필수 필드명 목록 (String 리스트)
     */
    List<String> getRequiredFieldsByRule(Rule rule);

    /**
     * 특정 룰에 연결된 모든 조건을 평가하여 결과 목록을 반환합니다.
     * <p>
     * 이 메서드는 하나의 룰에 연결된 모든 조건들을 가져와 평가하고,
     * 각 조건의 평가 결과를 ConditionResult 객체 리스트로 반환합니다.
     * </p>
     *
     * @param rule  평가할 룰 객체
     * @param facts 조건 평가에 사용할 입력 데이터
     * @return 조건 평가 결과 목록 (ConditionResult 리스트)
     */
    List<ConditionResult> evaluateConditionsForRule(Rule rule, Map<String, Object> facts);
}
