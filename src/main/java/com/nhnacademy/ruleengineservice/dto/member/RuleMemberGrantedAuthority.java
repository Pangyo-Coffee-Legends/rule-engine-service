package com.nhnacademy.ruleengineservice.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

/**
 * RuleMemberGrantedAuthority는 Spring Security의 GrantedAuthority를 구현한 클래스입니다.
 * <p>
 * 이 객체는 회원(mbNo)과 역할(roleNo, roleName) 정보를 함께 보유하여,
 * 인증 및 인가 과정에서 보다 풍부한 권한 정보를 제공합니다.
 * </p>
 *
 * <ul>
 *   <li>mbNo: 회원 고유 번호</li>
 *   <li>roleNo: 역할(권한) 고유 번호</li>
 *   <li>roleName: 역할(권한) 이름</li>
 * </ul>
 *
 * <p>
 * getAuthority() 메서드는 현재 빈 문자열을 반환하며,
 * 필요에 따라 roleName 등으로 구현을 변경할 수 있습니다.
 * </p>
 *
 * Lombok의 @Getter, @ToString, @AllArgsConstructor를 사용하여
 * 필드 접근자, toString(), 전체 필드 생성자를 자동 생성합니다.
 *
 * @author 강승우
 */
@Getter
@ToString
@AllArgsConstructor
public class RuleMemberGrantedAuthority implements GrantedAuthority {

    private final Long mbNo;

    private final Long roleNo;
    private final String roleName;

    @Override
    public String getAuthority() {
        return this.roleName;
    }
}
