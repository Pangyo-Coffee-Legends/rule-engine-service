package com.nhnacademy.ruleengineservice.dto.member;

import lombok.Value;

@Value
public class MemberResponse {
    Long no;

    String roleName;

    String name;

    String email;

    String password;

    String phoneNumber;
}
