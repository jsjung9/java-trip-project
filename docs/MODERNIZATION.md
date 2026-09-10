# 현대화 결과와 검증 근거

## 기준선

2023년 소스의 초기 점검 결과는 다음과 같았습니다.

- Spring Boot 2.7.17, Java 8, Springfox, JJWT 0.9.1
- 빈 context test 1개
- DB 계정·JWT 비밀값과 Kakao 키가 소스 트리에 존재
- 비밀번호를 단일 SHA-256+salt로 저장
- `@CrossOrigin("*")`과 Controller별 선택적 토큰 검사
- 요청의 `editorId`로 수정·삭제 대상을 결정
- 테마 생성 뒤 `MAX(theme_id)` 조회
- 좋아요 관계와 두 집계값을 Controller에서 따로 호출
- refresh token을 응답 JSON과 sessionStorage에 저장하고 콘솔에 출력
- npm audit: 총 15건(중간 3, 높음 11, 심각 1)
- 유지 중인 필수 ESLint 규칙 기준 오류 17건

## 개선 결과

| 영역 | 결과 |
| --- | --- |
| 플랫폼 | Spring Boot 3.5.16, Java 17, MyBatis starter 3.0.5, Vue 3.5, Vite 8 |
| 비밀값 | 현재 트리에서 제거하고 환경변수 예시 제공 |
| 인증 | Spring Security 필터, token type 검증, refresh HttpOnly 쿠키와 회전 |
| 비밀번호 | BCrypt 신규 저장, 기존 SHA-256 계정의 성공 로그인 시 자동 이관 |
| 권한 | JWT 사용자 기반 테마·장소 소유권 검사, 비공개 조회 차단 |
| 정합성 | 생성 키 반환, 좋아요·태그·장소·평점 트랜잭션, 관계 유일 키 |
| 프론트 | Axios 단일 인스턴스, 동시 refresh 공유, route lazy loading, 공유 DTO 제거 |
| 재현성 | Flyway V1/V2, Docker Compose, 비root 런타임 이미지 |
| 자동화 | GitHub Actions와 Dependabot |
| 품질 | 백엔드 13개·프론트 2개 테스트, ESLint 오류 0, npm audit 0 |

프론트는 기존 단일 152.31 kB JavaScript 번들에서 화면별 동적 청크 17개와 공통 청크로 분리되었습니다. 이 수치는 전체 전송량 감소를 뜻하지 않으며, 사용자가 방문하지 않은 화면 코드를 초기 경로에서 늦게 내려받게 한 결과입니다.

## 검증 명령과 결과

2026-09-10 로컬 환경에서 다음 검사를 통과했습니다.

```text
./mvnw clean verify                  13 tests, 0 failures
npm run lint                         0 errors, 0 warnings
npm run test                         2 tests, 0 failures
npm run build                        production build success
npm audit --audit-level=moderate     0 vulnerabilities
```

Docker daemon이 없는 작업 환경이어서 Compose 컨테이너 기동과 MySQL 대상 Flyway 실행은 로컬에서 수행하지 못했습니다. YAML 구문은 파싱했고, CI가 같은 애플리케이션 빌드·테스트를 다시 수행합니다. 기존 운영 DB에 V2를 적용할 때는 백업 사본에서 컬럼 타입과 제약 이름을 먼저 확인해야 합니다.
