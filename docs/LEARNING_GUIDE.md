# 코드·기술 학습 가이드

이 문서는 학습 설명을 코드 주석과 분리하기 위한 자료입니다. 코드 주석은 현재 구현의 이유를 알아야 유지보수할 수 있는 경우에만 남깁니다.

## 1. 인증 요청이 통과하는 과정

1. `SecurityConfig`가 공개 경로와 보호 경로를 구분합니다.
2. `JwtAuthenticationFilter`가 Bearer 접두사를 제거하고 `JWTUtil`로 서명, 만료, token type을 검증합니다.
3. 유효하면 JWT subject인 로그인 ID를 Spring Security의 `Authentication`에 넣습니다.
4. Controller는 `authentication.getName()`만 서비스에 전달합니다.
5. `EditorIdentity`가 로그인 ID를 DB의 내부 `editor_id`로 변환합니다.
6. 서비스는 이 값을 테마 소유자·기여자와 비교합니다.

이 구조에서 본문의 `editorId`를 바꿔 보내도 권한은 바뀌지 않습니다. JWT 검증을 Controller마다 반복하지 않아 누락되는 경로도 줄어듭니다.

## 2. 비밀번호 호환 이관

`PasswordService.matchesAndUpgrade`는 두 저장 형식을 판별합니다.

- `$2...`: BCrypt 해시이므로 `PasswordEncoder.matches` 사용
- 과거 64자리 SHA-256: 저장된 salt로 기존 해시를 비교하고 성공하면 BCrypt로 갱신

이관 업데이트는 `EditorServiceImpl.login`의 트랜잭션 안에서 실행됩니다. 실패한 로그인은 저장값을 바꾸지 않습니다. 새 회원과 정보 수정은 처음부터 BCrypt만 사용합니다.

## 3. 좋아요가 세 테이블을 건드리는 이유

원본 관계는 `like_theme(editor_id, theme_id)`입니다. 인기 조회를 빠르게 하기 위해 `theme.like_sum`과 테마 작성자의 `editor.like_sum`도 보관합니다. `ThemeServiceImpl.setLike`는 다음 순서를 하나의 트랜잭션으로 처리합니다.

1. `INSERT IGNORE` 또는 `DELETE`의 영향 행 수 확인
2. 0행이면 재시도 요청으로 보고 종료
3. 1행이면 테마 집계값 갱신
4. 테마 작성자 집계값 갱신

DB 복합 유일 키가 동시에 들어온 중복 요청을 차단하고, 서비스의 영향 행 검사가 집계값의 중복 증가를 막습니다. 감소에는 `GREATEST(value - 1, 0)`을 사용해 음수도 막습니다.

## 4. 평점을 평균이 아닌 원본으로 저장하는 이유

단순히 장소의 `score_sum`과 `score_count`만 증가시키면 같은 사용자가 새로고침하며 여러 번 평가할 수 있고 점수 수정도 어렵습니다. `place_score`는 사용자·장소별 원본 점수를 한 행으로 보관합니다. upsert 후 집계값을 다시 계산하므로 최초 평가와 수정이 같은 API로 동작합니다.

## 5. Axios 토큰 재발급 동시성

여러 컴포넌트가 동시에 401을 받으면 각각 refresh를 호출할 수 있습니다. refresh token을 회전하는 구조에서는 첫 호출 뒤 나머지가 과거 토큰을 보내 실패합니다. `http-commons.js`의 `refreshRequest`는 진행 중인 Promise를 공유합니다. 한 번 갱신한 access token을 모든 실패 요청이 받아 원 요청을 한 차례만 재시도합니다.

## 6. 생성 키와 동시성

과거 구현은 INSERT 뒤 `SELECT MAX(theme_id)`를 실행했습니다. 두 사용자가 비슷한 시각에 만들면 먼저 INSERT한 요청도 나중 요청의 ID를 받을 수 있습니다. MyBatis의 `useGeneratedKeys`와 `keyProperty="themeId"`는 같은 INSERT가 만든 키를 DTO에 직접 채웁니다.

## 7. Flyway baseline 전략

새 DB는 V1부터 전체 스키마를 생성합니다. 기존 비어 있지 않은 DB는 `baseline-on-migrate=true`, baseline version 1로 등록된 뒤 V2부터 실행합니다. V2는 기존 관계 중복을 정리하고 유일 키와 `place_score`를 추가합니다. 실제 운영 적용 전에는 DB 백업에서 V2를 리허설하고 기존 컬럼 타입을 확인해야 합니다.

## 8. 읽을 코드 순서

1. `theme-map/src/util/http-commons.js`
2. `ThemeMap/.../config/SecurityConfig.java`와 `JwtAuthenticationFilter.java`
3. `ThemeMap/.../editor/service/EditorIdentity.java`
4. `ThemeMap/.../theme/service/ThemeServiceImpl.java`
5. `ThemeMap/.../place/service/PlaceServiceImpl.java`
6. `ThemeMap/src/main/resources/mybatis/mapper/*.xml`
7. `ThemeMap/src/main/resources/db/migration/*.sql`
8. 각 서비스 테스트와 `theme-map/src/api/place.test.js`
