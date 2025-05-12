package com.nhnacademy.ruleengineservice.controller;

import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
import com.nhnacademy.ruleengineservice.dto.engine.RuleEvaluationResult;
import com.nhnacademy.ruleengineservice.schedule.ComfortInfoBuffer;
import com.nhnacademy.ruleengineservice.service.schedule.ComfortResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/comfort")
@RequiredArgsConstructor
public class ComfortController {
    private final ComfortInfoBuffer buffer;
    private final ComfortResultService comfortResultService;

    @PostMapping
    public ResponseEntity<Void> receiveComfortInfo(@RequestBody ComfortInfoDTO comfortInfo) {
        log.debug("받은 정보: {}", comfortInfo);
        buffer.add(comfortInfo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/scheduled-result")
    public ResponseEntity<List<RuleEvaluationResult>> getScheduledResult() {
        log.debug("scheduled-result success");
        return ResponseEntity.ok(comfortResultService.getLatestResults());
    }
}
