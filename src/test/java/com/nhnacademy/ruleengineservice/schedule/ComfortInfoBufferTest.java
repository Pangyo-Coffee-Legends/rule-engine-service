package com.nhnacademy.ruleengineservice.schedule;

import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComfortInfoBufferTest {

    @Test
    @DisplayName("버퍼가 정상 작동하는지 확인")
    void addAndDrainAll_ShouldStoreAndDrainElements() {
        ComfortInfoBuffer buffer = new ComfortInfoBuffer();
        ComfortInfoDTO dto1 = new ComfortInfoDTO(
                "A",
                25.0,
                50.0,
                400.0,
                "쾌적",
                "정상"
        );

        ComfortInfoDTO dto2 = new ComfortInfoDTO(
                "B",
                30.0,
                60.0, 600.0,
                "더움",
                "CO2 주의"
        );

        buffer.add(dto1);
        buffer.add(dto2);

        List<ComfortInfoDTO> drained = buffer.drainAll();

        assertEquals(List.of(dto1, dto2), drained);
        assertEquals(List.of(), buffer.drainAll());
    }
}