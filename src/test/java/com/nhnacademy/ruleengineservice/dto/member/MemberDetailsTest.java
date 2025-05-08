package com.nhnacademy.ruleengineservice.dto.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class MemberDetailsTest {

    @Test
    @DisplayName("constructor 와 getter 테스트")
    void constructorAndGetters() {
        String username = "test@example.com";
        RuleMemberGrantedAuthority authority = new RuleMemberGrantedAuthority(1L, 2L, "ROLE_USER");
        List<RuleMemberGrantedAuthority> authorityList = List.of(authority);

        MemberDetails memberDetails = new MemberDetails(username, authorityList);

        assertNotNull(memberDetails);
        assertAll(
                () -> assertEquals(username, memberDetails.getUsername()),
                () -> assertEquals(authorityList, memberDetails.getAuthorities()),
                () -> assertEquals("", memberDetails.getPassword())
        );
    }

    @Test
    @DisplayName("계정 상태 기본값 테스트")
    void isAccountNonExpired_isAccountNonLocked_isEnabled_defaultBehavior() {
        MemberDetails memberDetails = new MemberDetails("test@example.com", List.of());

        assertNotNull(memberDetails);
        assertAll(
                () -> assertTrue(memberDetails.isAccountNonExpired()),
                () -> assertTrue(memberDetails.isAccountNonLocked()),
                () -> assertTrue(memberDetails.isCredentialsNonExpired()),
                () -> assertTrue(memberDetails.isEnabled())
        );
    }

    @Test
    @DisplayName("equals 와 hashCode : username 과 authorities 가 같으면 동등성 보장")
    void equalsAndHashCode() {
        String username = "test@example.com";
        List<RuleMemberGrantedAuthority> authorities = List.of(
                new RuleMemberGrantedAuthority(1L, 2L, "ROLE_USER"));

        MemberDetails a = new MemberDetails(username, authorities);
        MemberDetails b = new MemberDetails(username, authorities);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        MemberDetails c = new MemberDetails("other@example.com", authorities);
        assertNotEquals(a, c);
    }
}