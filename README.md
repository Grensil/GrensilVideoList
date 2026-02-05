# Grensil Video List

Android application for browsing videos and photos from Pexels API.(https://www.pexels.com/api/)

## Features

- Browse popular videos and photos
- Bookmark favorite media
- Material Design 3 UI
- Multi-module architecture

## Architecture

- **Clean Architecture** with MVVM pattern
- **Multi-module** structure (app, core, feature modules)
- **Dependency Injection** with Hilt
- **Reactive Programming** with Kotlin Flow
- **Local Storage** with Room Database
- **Network** with Retrofit + OkHttp

## Setup

1. Clone the repository
```bash
git clone [repository-url]
cd GrensilVideoList
```

2. Get API Key from [Pexels](https://www.pexels.com/api/)

3. Create `local.properties` in the project root:
```properties
sdk.dir=/path/to/android/sdk
API_KEY=your_pexels_api_key_here
```

4. Build and run
```bash
./gradlew assembleDebug
```

## Module Structure

```
app/                    # Application module
core/
  ├── data/            # Data layer (repositories, data sources)
  ├── domain/          # Domain layer (use cases, models)
  ├── network/         # Network layer (API services)
  └── designsystem/    # Design system (themes, components)
feature/
  ├── main/            # Home screen feature
  └── bookmark/        # Bookmark feature
```

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **DI**: Hilt
- **Network**: Retrofit, OkHttp
- **Database**: Room
- **Image Loading**: Coil
- **Async**: Kotlin Coroutines + Flow
- **Paging**: Paging 3

## Security

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
- [📖 보안 설정 가이드](SECURITY_GUIDE.md) - 상세 보안 구현 가이드
- [✅ 보안 체크리스트](SECURITY_CHECKLIST.md) - Play Store 배포 전 체크리스트
- [🔑 Keystore 설정](KEYSTORE_SETUP.md) - 앱 서명 설정 가이드

## License

[Add your license here]
