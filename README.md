오늘 뭐 먹지?
===========
### 개요
음식 커뮤니티 및 AI API를 통한 음식 메뉴 추천 사이트입니다.  
이 프로젝트는 Spring Boot 기반의 웹 애플리케이션으로, 보안 강화된 사용자 인증 시스템과 서버 사이드 렌더링을 구현하였습니다.

### 기술 스택

- Backend: Java Spring Boot
- Database: MySQL
- ORM: MyBatis, JDBC Template
- Frontend: Thymeleaf (Server-Side Rendering)
- Authentication: JWT + OAuth2
- Security: Spring Security
- Testing: JUnit, Mockito
- Version Control: Git



주요 기능
--------------
### 보안 및 인증

- JWT와 OAuth2 기반의 사용자 인증 시스템 구현
- Spring Security를 활용한 보안 강화
    - SecurityContextHolder를 통한 Authentication 관리
    - 커스텀 JWT 인증 및 인가 필터 구현
      - JWTAuthentication Filter
      - JwtAuthorization Filter





### 프론트엔드

- Thymeleaf 템플릿 엔진을 사용한 서버 사이드 렌더링(SSR) 구현

### 데이터베이스 접근

MyBatis와 JDBC Template을 활용한 효율적인 데이터베이스 조작

### 테스트

- JUnit을 사용한 단위 테스트 구현
- Mockito의 Mock 객체를 활용한 격리된 테스트 환경 구성

개발 프로세스
--------------------------------------------
### 버전 관리
Git을 사용한 체계적인 버전 관리 시스템 구축
### 브랜치 전략
Git-flow에 기반한 브랜치 전략 채택:

- `main`: 프로덕션 환경의 안정적인 코드
- `develop`: 개발 진행 중인 최신 코드
- `feature/*`: 새로운 기능 개발 (예: feature/myPage)
- `bugfix/*`: 버그 수정
- `hotfix/*`: 긴급 수정 사항 처리

### 커밋 컨벤션
일관성 있는 커밋 메시지 작성을 위한 컨벤션 준수:
```
type: 간단한 설명

자세한 설명 (선택사항)

```
- type 예시: feat, fix, docs, style, refactor, test, chore

### 아키텍처

추후 예정
                                       
                          


### 코드 구조화 전략

- 레이어드 아키텍처 적용: Controller, Service, Repository 레이어 분리  
- SOLID 원칙 준수: 단일 책임 원칙, 개방-폐쇄 원칙 등을 고려한 설계  
- 의존성 주입을 통한 결합도 낮춤: Spring의 DI 컨테이너 활용  

### 보안 강화

- XSS 방지: Thymeleaf의 자동 이스케이핑 기능 활용  
- CSRF 방지: Spring Security의 CSRF 토큰 사용  
- SQL Injection 방지: PreparedStatement 및 MyBatis의 파라미터 바인딩 활용  

### 모니터링 및 로깅

추후 예정

### CI/CD

추후 예정

향후 개선 계획
=====================
- 마이크로서비스 아키텍처로의 전환 검토
- Kubernetes를 활용한 컨테이너 오케스트레이션 도입
- GraphQL API 구현을 통한 효율적인 데이터 요청 처리

- 데이터베이스 쿼리 최적화: 인덱스 활용 및 실행 계획 분석
- 캐싱 전략: Redis를 활용한 자주 접근하는 데이터 캐싱
- 비동기 처리: Spring의 @Async 어노테이션을 활용한 비동기 작업 처리

- Spring Actuator를 활용한 애플리케이션 상태 모니터링
- Logback을 이용한 체계적인 로그 관리
- ELK 스택 (Elasticsearch, Logstash, Kibana)을 활용한 로그 분석 시스템 구축
