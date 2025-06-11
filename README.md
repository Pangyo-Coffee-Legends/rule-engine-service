## **Rule Engine Service**

- Rule Engine Service는 Spring Boot와 Spring Cloud 기반으로, 다양한 비즈니스 규칙(룰)의 등록·관리·실행·알림을 지원하는 마이크로서비스입니다.
- RabbitMQ, Feign, JPA 등 최신 기술을 활용해 유연한 API 통합, 비동기 알림, 데이터 영속화 및 마이크로서비스 확장성을 제공합니다.

---

### **소개**

- Rule Engine Service는 다양한 도메인(센서, 사용자 등)에 적용할 수 있는 규칙(룰)과 그룹, 조건, 액션을 동적으로 관리하고, 실시간 데이터에 대해 룰 평가 및 자동화된 알림·제어를 제공하는 백엔드 서비스입니다.
- 외부 시스템(예: IoT Service)에서 들어오는 이벤트나 데이터에 따라 룰을 평가하고, 그 결과를 RabbitMQ, Feign 등으로 다른 마이크로서비스에 연동·전파할 수 있습니다.

---

### **주요 기능**

- 규칙 그룹 등록/조회/수정/삭제
- 규칙(룰) 등록/조회/수정/삭제
- 조건 및 액션 관리
- 룰 평가 및 결과 제공
- RabbitMQ를 통한 비동기 알림 발행
- Feign 기반 외부 서비스 연동
- Spring Cloud Eureka를 통한 서비스 디스커버리
- API 인증/인가 및 사용자 정보 ThreadLocal 관리

---
### **전체 흐름도**
![image](https://github.com/user-attachments/assets/a0694ddf-4f29-45a4-b0d2-f87af61c78ad)


---

### **아키텍처**

![image](https://github.com/user-attachments/assets/dd183dfb-06ae-4fd4-b53e-922d478f294c)

- REST API로 규칙/그룹/조건/액션 관리
- RabbitMQ로 알림/이벤트 비동기 송신
- Feign Client로 외부 서비스 연동
- JPA로 데이터 영속화
- Eureka로 서비스 등록 및 탐색

---

### **기술 스택**

![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![Feign](https://img.shields.io/badge/Feign_Client-006AFF?style=for-the-badge)
![Eureka](https://img.shields.io/badge/Eureka-212121?style=for-the-badge&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![H2 Database](https://img.shields.io/badge/H2_Database-0066A1?style=for-the-badge)
![Lombok](https://img.shields.io/badge/Lombok-A40000?style=for-the-badge)
![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ_IDEA-000000?style=for-the-badge&logo=intellij-idea&logoColor=white)



---
## 기여자
| <img src="./src/main/resources/static/images/woo.png" width="300" height="300" alt="woo"/> | <img src="./src/main/resources/static/images/ho.png" width="300" height="300" alt="ho"/> |
|--------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| [강승우의 GitHub](https://github.com/oculusK)                                                  | [박형호의 GitHub](https://github.com/phh624)                                                |
