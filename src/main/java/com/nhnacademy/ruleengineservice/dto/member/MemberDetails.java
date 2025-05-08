package com.nhnacademy.ruleengineservice.dto.member;

import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * MemberDetails 클래스는 Spring Security의 UserDetails 인터페이스를 구현하여
 * 인증 및 권한 부여에 필요한 사용자 정보를 캡슐화합니다.
 * 이 클래스는 외부 시스템에서 조회한 회원 정보(username)와
 * 해당 회원의 권한 목록(RuleMemberGrantedAuthority)을 포함합니다.
 * 주요 기능:
 * <ul>
 *   <li>사용자의 권한(GrantedAuthority) 제공</li>
 *   <li>사용자 이름과 비밀번호 반환</li>
 *   <li>계정 상태 관련 메서드 구현 (만료, 잠금, 활성화 등)</li>
 * </ul>
 *
 * equals()와 hashCode() 메서드를 오버라이드하여 객체 동등성 비교 시
 * username 과 권한 리스트를 기준으로 판단합니다.
 * Lombok의 @ToString과 @NoArgsConstructor 어노테이션을 사용하여
 * toString()과 기본 생성자를 자동 생성합니다.
 *
 * @author 강승우
 */
@ToString
@NoArgsConstructor
public class MemberDetails implements UserDetails {

    private String username;
    private List<RuleMemberGrantedAuthority> ruleMemberGrantedAuthorities;

    /**
     * MemberDetails의 생성자입니다.
     * 이 생성자는 인증된 회원의 정보와 권한 목록을 받아
     * Spring Security의 UserDetails 구현체(MemberDetails)로 초기화합니다.
     *
     * @param username
     *      외부 회원 서비스(MemberAdaptor 등)에서 조회한 회원의 이메일입니다.
     * @param ruleMemberGrantedAuthorities
     *      해당 회원이 보유한 권한(역할) 정보의 리스트입니다.
     *      각 권한 객체는 Spring Security의 GrantedAuthority를 구현하며,
     *      회원의 역할 번호, 역할 이름 등 인가(authorization)에 필요한 정보를 담고 있습니다.
     */
    public MemberDetails(String username, List<RuleMemberGrantedAuthority> ruleMemberGrantedAuthorities) {
        this.username = username;
        this.ruleMemberGrantedAuthorities = ruleMemberGrantedAuthorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return ruleMemberGrantedAuthorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        MemberDetails that = (MemberDetails) object;
        return Objects.equals(username, that.username) && Objects.equals(ruleMemberGrantedAuthorities, that.ruleMemberGrantedAuthorities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, ruleMemberGrantedAuthorities);
    }
}
