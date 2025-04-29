package com.nhnacademy.ruleengineservice.service.engine.impl;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.service.action.ActionService;
import com.nhnacademy.ruleengineservice.service.engine.ExecutionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ExecutionServiceImpl implements ExecutionService {

    private final ActionService actionService;

    public ExecutionServiceImpl(ActionService actionService) {
        this.actionService = actionService;
    }

    @Override
    public List<ActionResult> executeActions(Rule rule, Map<String, Object> facts) {
        List<ActionResult> results = new ArrayList<>();

        for (Action action : rule.getActionList()) {
            results.add(executeAction(action, facts));
        }

        return results;
    }

    @Override
    public ActionResult executeAction(Action action, Map<String, Object> context) {
        return actionService.performAction(action.getActNo(), context);
    }
}
