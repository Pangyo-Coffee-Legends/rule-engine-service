package com.nhnacademy.ruleengineservice.dto.parameter;

/**
 * 파라미터(Parameter) 등록 요청을 위한 DTO 클래스입니다.
 * <p>
 * 클라이언트가 새로운 룰 파라미터를 생성할 때 필요한 정보를 전달하는 데 사용됩니다.
 * 각 파라미터는 특정 규칙(Rule)에 속하며, 파라미터 이름과 값을 포함합니다.
 * </p>
 * <p>
 * <b>연관 테이블/필드:</b>
 * <ul>
 *   <li>ruleNo: rule_parameters 테이블의 rule_no (rules 테이블 외래키)</li>
 *   <li>paramName: rule_parameters 테이블의 param_name</li>
 *   <li>paramValue: rule_parameters 테이블의 param_value</li>
 * </ul>
 * </p>
 *
 * @author 강승우
 */
public class ParameterRegisterRequest {

    /**
     * 파라미터가 속할 규칙의 식별자입니다.
     * rule_parameters 테이블의 rule_no 컬럼과 매핑되며, rules 테이블을 참조합니다.
     */
    private Long ruleNo;

    /**
     * 파라미터의 이름입니다.
     * rule_parameters 테이블의 param_name 컬럼과 매핑됩니다.
     */
    private String paramName;

    /**
     * 파라미터의 값입니다.
     * rule_parameters 테이블의 param_value 컬럼과 매핑됩니다.
     */
    private String paramValue;

    /**
     * 모든 필드를 초기화하는 생성자입니다.
     *
     * @param ruleNo     파라미터가 속할 규칙의 식별자
     * @param paramName  파라미터 이름
     * @param paramValue 파라미터 값
     */
    public ParameterRegisterRequest(Long ruleNo, String paramName, String paramValue) {
        this.ruleNo = ruleNo;
        this.paramName = paramName;
        this.paramValue = paramValue;
    }

    public Long getRuleNo() {
        return ruleNo;
    }

    public String getParamName() {
        return paramName;
    }

    public String getParamValue() {
        return paramValue;
    }
}
