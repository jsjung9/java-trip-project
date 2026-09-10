# API 명세

기본 URL은 `http://localhost:8080`입니다. 보호 API는 `Authorization: Bearer <access-token>` 헤더가 필요합니다. refresh token은 JavaScript에서 읽지 않는 `refresh_token` 쿠키로만 전달됩니다.

## Editor

| Method | Path | 인증 | 설명 |
| --- | --- | --- | --- |
| POST | `/editor/regist` | 없음 | 회원가입 |
| POST | `/editor/login` | 없음 | 로그인, access token과 refresh cookie 발급 |
| POST | `/editor/refresh` | 쿠키 | 두 토큰 회전 |
| POST | `/editor/logout` | 필요 | 저장된 refresh token 제거 및 쿠키 만료 |
| GET | `/editor/info` | 필요 | 현재 사용자 정보 |
| PATCH | `/editor/modify` | 필요 | 현재 사용자 정보·비밀번호 변경 |
| DELETE | `/editor/resign` | 필요 | 현재 사용자 탈퇴 |
| GET | `/editor/name/{editorId}` | 없음 | 공개 표시 이름 조회 |
| GET | `/editor/power` | 없음 | 좋아요 합계 상위 5명 조회 |

로그인 응답 예시:

```json
{
  "access-token": "eyJ...",
  "editor": {
    "editorId": "7",
    "id": "map_user",
    "editorName": "지도편집자",
    "likeSum": "3"
  }
}
```

## Theme

| Method | Path | 인증 | 설명 |
| --- | --- | --- | --- |
| GET | `/theme/all` | 없음 | 장소가 하나 이상인 공개 테마 |
| GET | `/theme/hot` | 없음 | 좋아요가 있는 공개 테마 상위 10개 |
| GET | `/theme/get/{themeId}` | 선택 | 공개 상세 또는 작성자의 비공개 상세 |
| GET | `/theme/place/{placeId}` | 없음 | 장소가 포함된 공개 테마 |
| GET | `/theme/visible/{editorId}` | 없음 | 특정 작성자의 공개 테마 |
| GET | `/theme/editor/{editorId}` | 필요 | 본인의 전체 테마 |
| GET | `/theme/like/{editorId}` | 필요 | 본인이 좋아요 한 테마 |
| POST | `/theme/create` | 필요 | 테마 생성, 생성된 ID 반환 |
| PUT | `/theme/update` | 필요 | 작성자의 테마 수정 |
| DELETE | `/theme/delete/{themeId}` | 필요 | 작성자의 테마 삭제 |
| POST | `/theme/postLike` | 필요 | 좋아요 멱등 등록 |
| POST | `/theme/disLike` | 필요 | 좋아요 멱등 해제 |
| GET | `/theme/didLike/{editorId}/{themeId}` | 필요 | 현재 사용자의 좋아요 여부 |
| GET | `/theme/allTags` | 없음 | 태그 목록 |
| POST | `/theme/tag` | 없음 | 모든 선택 태그를 포함한 공개 테마 검색 |
| GET | `/theme/tagsOfTheme/{themeId}` | 없음 | 테마 태그 목록 |
| POST | `/theme/updateTag/{themeId}` | 필요 | 작성자의 태그 전체 교체 |

`editorId`를 받는 보호 경로는 기존 클라이언트 호환을 위해 경로 변수를 유지하지만, 권한 판단에는 JWT 사용자를 사용합니다.

## Place와 Comment

| Method | Path | 인증 | 설명 |
| --- | --- | --- | --- |
| GET | `/place/hot` | 없음 | 평가 6건 이상인 상위 장소 10개 |
| GET | `/place/theme/{themeId}` | 없음 | 테마의 장소 목록 |
| POST | `/place/create` | 필요 | Kakao 장소 멱등 저장 |
| POST | `/place/link` | 필요 | 테마에 장소 연결 및 기여 한도 검사 |
| DELETE | `/place/delete/{themeId}/{placeId}` | 필요 | 권한이 있는 장소 관계 삭제 |
| PUT | `/place/score` | 필요 | 1~5점 평가 생성 또는 변경 |
| GET | `/place/isThere/{placeId}` | 없음 | 장소 저장 여부 |
| GET | `/place/isInTheme/{themeId}/{placeId}` | 없음 | 테마 연결 여부 |
| GET | `/place/who/{themeId}/{placeId}` | 없음 | 장소 기여자 ID |
| GET | `/place/spare/{themeId}/{editorId}` | 필요 | 현재 사용자의 기존 기여 수 |
| GET | `/comment/comments/{placeId}` | 없음 | 장소 댓글 목록 |
| POST | `/comment/regist` | 필요 | 500자 이하 댓글 등록 |

오류 응답은 `{"message":"..."}` 형태입니다. 입력 오류 400, 인증 실패 401, 권한 부족 403, 없음 404, 중복·한도 충돌 409를 사용합니다.
