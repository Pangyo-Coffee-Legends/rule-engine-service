package com.nhnacademy.ruleengineservice.adaptor;

import com.nhnacademy.ruleengineservice.dto.member.MemberInfoResponse;
import com.nhnacademy.ruleengineservice.dto.member.MemberResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 회원(Member) 서비스와의 통신을 담당하는 Feign Client 어댑터 인터페이스입니다.
 * <p>
 * 이 인터페이스는 외부 member-service의 REST API를 호출하여
 * 회원의 상세 정보 및 회원 목록 정보를 조회하는 기능을 제공합니다.
 * </p>
 *
 * <ul>
 *   <li><b>getMemberByEmail</b>: 이메일(고유 식별자)로 특정 회원의 상세 정보를 조회합니다.</li>
 *   <li><b>getMemberInfoList</b>: 전체 회원의 요약 정보 목록을 조회합니다.</li>
 * </ul>
 *
 * <p>
 * <b>사용 예시:</b>
 * <pre>
 *     MemberResponse member = memberAdaptor.getMemberByEmail("test@example.com").getBody();
 *     List&lt;MemberInfoResponse&gt; members = memberAdaptor.getMemberInfoList().getBody();
 * </pre>
 * </p>
 *
 * @author 강승우
 */
@FeignClient(
        name = "member-service",
        url = "${member-service.url}",
        path = "/api/v1/members"
)
public interface MemberAdaptor {

    /**
     * 이메일(회원 고유 번호)로 특정 회원의 상세 정보를 조회합니다.
     *
     * @param mbEmail 조회할 회원의 이메일(고유 식별자, PathVariable)
     * @return 해당 회원의 상세 정보가 담긴 ResponseEntity (HTTP 200 OK)
     */
    @GetMapping("/email/{mbEmail}")
    ResponseEntity<MemberResponse> getMemberByEmail(@PathVariable String mbEmail);

    /**
     * 전체 회원의 요약 정보 목록을 조회합니다.
     *
     * @return 회원 요약 정보 리스트가 담긴 ResponseEntity (HTTP 200 OK)
     */
    @GetMapping("/info-list")
    ResponseEntity<List<MemberInfoResponse>> getMemberInfoList();
}
