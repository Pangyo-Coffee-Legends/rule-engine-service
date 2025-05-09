package com.nhnacademy.ruleengineservice.controller;

import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
import com.nhnacademy.ruleengineservice.schedule.ComfortInfoBuffer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/comfort")
@RequiredArgsConstructor
public class ComfortController {
    private final ComfortInfoBuffer buffer;

    @PostMapping
    public ResponseEntity<Void> receiveComfortInfo(@RequestBody ComfortInfoDTO comfortInfo) {
        log.debug("받은 정보: {}", comfortInfo);
        buffer.add(comfortInfo);
        return ResponseEntity.ok().build();
    }
}
