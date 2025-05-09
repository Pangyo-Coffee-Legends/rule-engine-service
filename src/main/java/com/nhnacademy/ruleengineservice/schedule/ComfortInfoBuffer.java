package com.nhnacademy.ruleengineservice.schedule;

import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class ComfortInfoBuffer {
    private final Queue<ComfortInfoDTO> buffer = new ConcurrentLinkedQueue<>();

    public void add(ComfortInfoDTO info) {
        buffer.add(info);
    }

    public List<ComfortInfoDTO> drainAll() {
        List<ComfortInfoDTO> list = new ArrayList<>();
        ComfortInfoDTO info;

        while((info = buffer.poll()) != null) {
            list.add(info);
        }

        return list;
    }
}
