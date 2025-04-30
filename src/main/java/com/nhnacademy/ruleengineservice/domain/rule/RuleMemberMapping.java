package com.nhnacademy.ruleengineservice.domain.rule;

import jakarta.persistence.*;
import lombok.Getter;

/**
 * RuleMemberMapping 엔티티는 규칙과 멤버(사용자) 간의 매핑 정보를 저장합니다.
 * 한 규칙에 여러 멤버가 연결될 수 있고, 한 멤버가 여러 규칙에 연결될 수 있습니다.
 */
@Entity
@Getter
@Table(name = "rule_member_mappings")
public class RuleMemberMapping {

    /**
     * 매핑의 고유 식별자(PK).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapping_no")
    private Long mappingNo;

    /**
     * 매핑된 규칙(Rule) 엔티티.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_no", referencedColumnName = "rule_no", nullable = false)
    private Rule rule;

    /**
     * 매핑된 멤버(사용자) 식별자.
     * 실제 멤버 정보는 외부 시스템(API)에서 관리합니다.
     */
    @Column(name = "mb_no", nullable = false)
    private Long mbNo;

    /**
     * JPA 기본 생성자 (외부에서 직접 호출하지 않음).
     */
    protected RuleMemberMapping() {}

    /**
     * RuleMemberMapping 객체의 생성자.
     *
     * @param rule 규칙
     * @param mbNo 멤버의 번호
     */
    private RuleMemberMapping(Rule rule, Long mbNo) {
        this.rule = rule;
        this.mbNo = mbNo;
    }

    /**
     * RuleMemberMapping 객체를 생성하는 정적 팩토리 메서드.
     *
     * @param rule 규칙
     * @param mbNo 멤버의 번호
     * @return 새 RuleMemberMapping 인스턴스
     */
    public static RuleMemberMapping ofNewRuleMemberMapping(Rule rule, Long mbNo) {
        return new RuleMemberMapping(rule, mbNo);
    }

    @Override
    public String toString() {
        return "RuleMemberMapping{" +
                "mappingNo=" + mappingNo +
                ", rule=" + rule +
                ", mbNo=" + mbNo +
                '}';
    }
}
