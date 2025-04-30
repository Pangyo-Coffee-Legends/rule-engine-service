package com.nhnacademy.ruleengineservice.service.parameter;

import com.nhnacademy.ruleengineservice.dto.parameter.ParameterRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.parameter.ParameterResponse;

import java.util.List;

/**
 * 파라미터(Parameter) 도메인 객체의 비즈니스 로직을 담당하는 서비스 인터페이스입니다.
 * <p>
 * 이 인터페이스는 rule_parameters 테이블의 CRUD 연산,
 * 특정 룰에 연결된 파라미터 관리, 파라미터 값 바인딩 및 파라미터 조회 등
 * 핵심 비즈니스 기능을 정의합니다.
 * </p>
 * <p>
 * 주요 역할:
 * <ul>
 *   <li>파라미터 등록, 수정, 삭제, 단건/목록 조회</li>
 *   <li>특정 룰에 연결된 파라미터 목록 조회</li>
 *   <li>파라미터 값 동적 바인딩 및 검증</li>
 *   <li>룰 엔진과의 연동(룰 평가 시 파라미터 활용 등)</li>
 * </ul>
 * </p>
 *
 * @author 강승우
 */
public interface ParameterService {

    /**
     * 새로운 파라미터를 등록합니다.
     * @param request 파라미터 등록 요청 DTO
     * @return 생성된 상세 DTO
     */
    ParameterResponse registerParameter(ParameterRegisterRequest request);

    /**
     * 파라미터를 삭제합니다.
     * @param paramNo 파라미터 식별자
     */
    void deleteParameter(Long paramNo);

    /**
     * 파라미터 단건 조회
     * @param paramNo 파라미터 식별자
     * @return 파라미터 상세 DTO
     */
    ParameterResponse getParameter(Long paramNo);

    /**
     * 특정 룰에 연결된 파라미터 목록 조회
     * @param ruleNo 룰 식별자
     * @return 파라미터 응답 DTO 리스트
     */
    List<ParameterResponse> getParametersByRule(Long ruleNo);

    /**
     * 파라미터 값을 동적으로 바인딩(룰 실행 시점에 값 주입)
     * @param paramNo 파라미터 식별자
     * @param value 바인딩할 값
     */
    void bindParameterValue(Long paramNo, String value);
}
