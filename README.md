Yummuity (얌뮤티)
===========
### 개요
맛집을 찾고 공유하는 과정에서 성능과 사용성을 모두 고려한 커뮤니티 서비스입니다.
특히 동시 접속자가 많은 상황에서도 안정적인 서비스 제공을 위해 다양한 성능 최적화를 진행했습니다.

### 기술 스택

- Backend: OpenJDK 17, Spring Boot 3.2.2, Spring Security 3.2.2
- Database: MySQL
- ORM: MyBatis, JdbcTemplate
- Frontend: Thymeleaf (Server-Side Rendering)
- Authentication: JWT, OAuth2.0
- Infrastructure: AWS EC2, Docker, Nginx
- Testing: K6 부하 테스트, Junit6, mockito
- Version Control: Git


### 서비스 아키텍처
![123](https://github.com/user-attachments/assets/ee807e9a-787d-4f72-a43f-387824259aa8)

### Entity Diagram
![image.png](attachment:a7ef82a2-9ef8-41ab-af26-0422c7f825da:image.png)

주요 기능
--------------
### 성능 개선

- K6로 200명 동시접속 상황 테스트
- Nginx 캐시로 평균 응답시간 65% 개선
- BCrypt 설정 최적화로 CPU 사용률 79% → 37%
- 리뷰 조회 N+1 문제 해결로 응답시간 82% 단축


### 캐시 전략

- 정적/동적 리소스 분리해서 캐시 적용
- ETag 기반 브라우저 캐싱 구현
- Keepalive로 연결 재사용 최적화


### 서비스 안정성

- 필터링 중 발생하는 데이터 충돌 해결
- Promise 기반 렌더링 작업 관리
- Docker & Nginx로 무중단 배포 구현

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


                                       
### 코드 구조화 전략

- 레이어드 아키텍처 적용: Controller, Service, Repository 레이어 분리  
- SOLID 원칙 준수: 단일 책임 원칙, 개방-폐쇄 원칙 등을 고려한 설계  
- 의존성 주입을 통한 결합도 낮춤: Spring의 DI 컨테이너 활용  

### 보안 강화

- XSS 방지: Thymeleaf의 자동 이스케이핑 기능 활용  
- CSRF 방지: Spring Security의 CSRF 토큰 사용  


### 모니터링 및 로깅

- AWS CloudWatch를 통한 서버의 리소스를 모니터링하여 병목 지점을 확인할 수 있도록 설정


### 배포

- EC2에 Docker 컨테이너로 구성
- Nginx 리버스 프록시 설정
- Docker를 통해 컨테이너 세팅 후, nginx 포트 변경을 이용하는 blue-green 전략 / 무중단 배포
- certbot 라이브러리를 통해 ssl 인증서 발급에 대한 값을 ec2 호스트에 설정하여 nginx 웹 서버에 볼륨 마운트 지정


향후 개선 계획
=====================
- 데이터베이스 쿼리 최적화: 인덱스 활용 및 실행 계획 분석
- 캐싱 전략: Redis를 활용한 자주 접근하는 데이터 캐싱
