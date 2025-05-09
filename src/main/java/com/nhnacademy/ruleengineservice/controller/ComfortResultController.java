package com.nhnacademy.ruleengineservice.controller;

import com.nhnacademy.ruleengineservice.dto.engine.RuleEvaluationResult;
import com.nhnacademy.ruleengineservice.service.schedule.ComfortResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comfort")
@RequiredArgsConstructor
public class ComfortResultController {
    private final ComfortResultService comfortResultService;

    @GetMapping("/scheduled-result")
    public ResponseEntity<List<RuleEvaluationResult>> getScheduledResult() {
        return ResponseEntity.ok(comfortResultService.getLatestResults());
    }
}
