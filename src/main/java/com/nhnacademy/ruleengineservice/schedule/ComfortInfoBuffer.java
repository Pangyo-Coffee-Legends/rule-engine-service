package com.nhnacademy.ruleengineservice.schedule;

import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * {@code ComfortInfoBuffer}는 IoT 센서 등에서 수집된 {@link ComfortInfoDTO} 데이터를 임시로 저장하는 버퍼 역할의 컴포넌트입니다.
 * <p>
 * 내부적으로 스레드 안전한 {@link ConcurrentLinkedQueue}를 사용하여 다중 스레드 환경에서도 안전하게 데이터를 추가(add) 및 일괄 추출(drainAll)할 수 있습니다.
 * <ul>
 *   <li>{@link #add(ComfortInfoDTO)}: 버퍼에 새 데이터를 추가합니다.</li>
 *   <li>{@link #drainAll()}: 버퍼에 쌓인 모든 데이터를 리스트로 반환하고, 버퍼를 비웁니다.</li>
 * </ul>
 * API 연동, IoT 데이터 수집, Spring Boot 기반 서비스 등 다양한 환경에서 활용할 수 있습니다[1][2][3].
 * </p>
 *
 * @author 강승우
 * @since 1.0
 */
@Component
public class ComfortInfoBuffer {
    private final Queue<ComfortInfoDTO> buffer = new ConcurrentLinkedQueue<>();

    /**
     * 버퍼에 {@link ComfortInfoDTO} 데이터를 추가합니다.
     *
     * @param info 추가할 편의 정보 데이터 객체
     */
    public void add(ComfortInfoDTO info) {
        buffer.add(info);
    }

    /**
     * 버퍼에 저장된 모든 {@link ComfortInfoDTO} 데이터를 리스트로 반환하고, 버퍼를 비웁니다.
     *
     * @return 버퍼에 저장된 모든 데이터의 리스트
     */
    public List<ComfortInfoDTO> drainAll() {
        List<ComfortInfoDTO> list = new ArrayList<>();
        ComfortInfoDTO info;

        while((info = buffer.poll()) != null) {
            list.add(info);
        }

        return list;
    }
}
