# Grensil Video List

Pexels API를 활용한 비디오 및 사진 브라우징 Android 애플리케이션입니다. (https://www.pexels.com/api/)

## 주요 기능

- 인기 비디오 및 사진 탐색
- 유튜브 스타일 자동 재생 프리뷰
- 비디오 상세 재생 (전체화면 지원)
- 이미지 확대 보기 (저장/공유 기능)
- 즐겨찾기 북마크 기능
- Material Design 3 UI
- 멀티 모듈 아키텍처

## 아키텍처

- **Clean Architecture** + MVVM 패턴
- **멀티 모듈** 구조 (app, core, feature 모듈)
- **의존성 주입** - Hilt
- **반응형 프로그래밍** - Kotlin Flow
- **로컬 저장소** - Room Database
- **네트워크** - Retrofit + OkHttp

## 설정 방법

1. 저장소 클론
```bash
git clone [repository-url]
cd GrensilVideoList
```

2. [Pexels](https://www.pexels.com/api/)에서 API Key 발급

3. 프로젝트 루트에 `local.properties` 생성:
```properties
sdk.dir=/path/to/android/sdk
API_KEY=your_pexels_api_key_here
```

4. 빌드 및 실행
```bash
./gradlew assembleDebug
```

## 모듈 구조

```
app/                    # 애플리케이션 모듈
core/
  ├── data/            # 데이터 레이어 (Repository, DataSource)
  ├── domain/          # 도메인 레이어 (UseCase, Model)
  ├── network/         # 네트워크 레이어 (API 서비스)
  ├── player/          # 비디오 플레이어 (ExoPlayer 관리)
  └── designsystem/    # 디자인 시스템 (테마, 컴포넌트)
feature/
  ├── main/            # 홈 화면 및 비디오 상세
  └── bookmark/        # 북마크 기능
```

## 기술 스택

| 분류 | 기술 |
|------|------|
| **언어** | Kotlin |
| **UI** | Jetpack Compose |
| **DI** | Hilt |
| **네트워크** | Retrofit, OkHttp |
| **데이터베이스** | Room |
| **이미지 로딩** | Coil |
| **비동기** | Kotlin Coroutines + Flow |
| **페이징** | Paging 3 |
| **비디오** | Media3 ExoPlayer |

## 보안

- **Room Database Encryption** - SQLCipher를 통한 DB 암호화
- **Screen Security** - 스크린샷 및 화면 녹화 방지 (Release 빌드)
- **Root / Emulator Detection** - 루팅 기기 및 에뮬레이터 탐지
- **Network Security Config** - HTTPS 강제

## 라이선스

MIT License
