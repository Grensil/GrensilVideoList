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

이 앱은 **금융앱 수준의 보안**을 적용하고 있습니다.

### 네트워크 보안
- ✅ **Certificate Pinning** - MITM 공격 방지
- ✅ **Network Security Config** - HTTP 트래픽 차단
- ✅ **SSL/TLS only** - HTTPS 강제

### 데이터 보안
- ✅ **EncryptedSharedPreferences** - 민감 데이터 암호화 저장
- ✅ **Room Database Encryption** - SQLCipher를 통한 DB 암호화
- ✅ **Secure Key Management** - Android Keystore 활용

### 런타임 보안
- ✅ **Root Detection** - 루팅된 기기 탐지
- ✅ **Debug Prevention** - 디버깅 방지
- ✅ **Screen Security** - 스크린샷 및 화면 녹화 방지 (Release)
- ✅ **Emulator Detection** - 에뮬레이터 탐지

### 코드 보안
- ✅ **ProGuard/R8** - 코드 난독화 및 최적화
- ✅ **API Key Obfuscation** - API 키 난독화
- ✅ **Debug Log Removal** - Release 빌드에서 로그 제거

### 앱 무결성
- ✅ **Google Play Integrity API** - 앱 무결성 검증
- ✅ **App Signing** - V1/V2/V3/V4 서명

### 보안 문서
자세한 보안 설정은 다음 문서를 참고하세요:
- [보안 설정 가이드](SECURITY_GUIDE.md) - 상세 보안 구현 가이드
- [보안 체크리스트](SECURITY_CHECKLIST.md) - Play Store 배포 전 체크리스트
- [Keystore 설정](KEYSTORE_SETUP.md) - 앱 서명 설정 가이드

## 라이선스

MIT License
