package com.nhnacademy.ruleengineservice.dto.member;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class MemberResponse {
    @NotNull
    Long no;

    String roleName;

    String name;

    String email;

    String password;

    String phoneNumber;
}
