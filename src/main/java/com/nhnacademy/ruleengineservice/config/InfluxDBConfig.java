package com.nhnacademy.ruleengineservice.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDBConfig {

    /**
     * InfluxDB 서버의 접속 URL 입니다.
     * ex) <a href="http://localhost:8086">http://localhost:8086</a>
     */
    @Value("${influx.url}")
    private String url;

    /**
     * InfluxDB API 토큰입니다.
     * 인증 권한 및 권한 부여에 사용됩니다.
     */
    @Value("${influx.token}")
    private String token;

    /**
     * InfluxDB의 조직 이름 입니다.
     * 여러 사용자 / 버킷을 그룹화하는 단위입니다.
     */
    @Value("${influx.org}")
    private String org;

    /**
     * InfluxDB의 버킷 입니다.
     * 데이터베이스 + 보존 역할을 합니다.
     */
    @Value("${influx.bucket}")
    private String bucket;

    /**
     * InfluxDB 서버와의 연결을 생성합니다.
     *
     * @return InfluxDBClientFactory.create() 메서드를 통해 클라이언트 인스턴스를 반환
     */
    @Bean
    public InfluxDBClient influxDBClient() {
        return InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
    }

    /**
     * InfluxDB에서 데이터를 조회하는 기능을 제공합니다.
     *
     * @param client InfluxDB의 Client 입니다.
     * @return Flux 쿼리 언어를 사용해 시계열 데이터를 조회합니다.
     */
    @Bean
    public QueryApi queryApi(InfluxDBClient client) {
        return client.getQueryApi();
    }
}
