# 금융앱 보안 설정 가이드

이 문서는 금융앱을 Play Store에 배포하기 위한 필수 보안 설정 가이드입니다.

## 📋 목차

1. [네트워크 보안](#1-네트워크-보안)
2. [API 키 및 민감정보 관리](#2-api-키-및-민감정보-관리)
3. [데이터 암호화](#3-데이터-암호화)
4. [런타임 보안](#4-런타임-보안)
5. [코드 난독화](#5-코드-난독화)
6. [앱 무결성 검증](#6-앱-무결성-검증)
7. [배포 전 체크리스트](#7-배포-전-체크리스트)

---

## 1. 네트워크 보안

### Certificate Pinning 설정

**위치**: `app/src/main/res/xml/network_security_config.xml`

```bash
# 인증서 핀 추출 방법
echo | openssl s_client -connect api.pexels.com:443 2>/dev/null | \
  openssl x509 -pubkey -noout | \
  openssl pkey -pubin -outform der | \
  openssl dgst -sha256 -binary | base64
```

**설정 파일에 실제 핀 값 추가:**
```xml
<pin digest="SHA-256">실제_핀_값</pin>
```

### Network Security Config 검증

- ✅ HTTP 트래픽 차단 (`cleartextTrafficPermitted="false"`)
- ✅ 시스템 인증서만 신뢰
- ✅ Certificate Pinning 설정
- ✅ 백업 핀 포함 (인증서 갱신 대비)

---

## 2. API 키 및 민감정보 관리

### API 키 보안

**현재 구현**: Base64 난독화 + ProGuard
**위치**: `core/network/src/main/java/com/example/network/security/ApiKeyProvider.kt`

#### API 키 난독화 방법:

```bash
echo -n "YOUR_ACTUAL_API_KEY" | base64
```

결과값을 `ApiKeyProvider.kt`의 `encoded` 변수에 입력하세요.

### 더 강력한 보안 (권장)

**NDK를 사용한 네이티브 보안:**

1. C/C++로 API 키 저장
2. JNI를 통해 접근
3. 네이티브 라이브러리 난독화

```cpp
// native-lib.cpp 예시
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_network_NativeKeys_getApiKey(JNIEnv* env, jobject) {
    return env->NewStringUTF("YOUR_API_KEY");
}
```

### local.properties 보안

```properties
# local.properties (절대 Git에 커밋하지 말 것!)
API_KEY=your_api_key_here
KEYSTORE_PASSWORD=your_keystore_password
KEY_PASSWORD=your_key_password
```

**Git 보안:**
```bash
# .gitignore에 추가 확인
grep "local.properties" .gitignore
```

---

## 3. 데이터 암호화

### EncryptedSharedPreferences

**위치**: `core/data/src/main/java/com/example/data/security/SecurePreferences.kt`

**사용 예시:**
```kotlin
@Inject
lateinit var securePreferences: SecurePreferences

// 저장
securePreferences.putString(SecurePreferences.KEY_USER_TOKEN, token)

// 읽기
val token = securePreferences.getString(SecurePreferences.KEY_USER_TOKEN)
```

### Room 데이터베이스 암호화 (SQLCipher)

**위치**: `core/data/src/main/java/com/example/data/security/DatabaseEncryptionHelper.kt`

**Database Builder에 적용:**
```kotlin
Room.databaseBuilder(context, AppDatabase::class.java, "database")
    .openHelperFactory(databaseEncryptionHelper.getEncryptedFactory())
    .build()
```

**체크리스트:**
- ✅ SQLCipher 의존성 추가
- ✅ Application에서 초기화
- ✅ 암호키 안전하게 저장 (EncryptedFile 사용)
- ✅ 백업 비활성화

---

## 4. 런타임 보안

### Root 탐지

**위치**: `app/src/main/java/com/example/grensilvideolist/security/SecurityChecker.kt`

**검사 항목:**
- ✅ su 바이너리 존재 여부
- ✅ test-keys 빌드 태그
- ✅ 루팅 앱 설치 여부 (SuperSU, Magisk 등)

### 화면 보안

**구현된 기능:**
- ✅ 스크린샷 방지 (`FLAG_SECURE`)
- ✅ 화면 녹화 방지
- ✅ 최근 앱 목록 블러 처리

**적용 위치**: `MainActivity.kt`

### 디버깅 방지

Release 빌드에서:
- ✅ `isDebuggable = false`
- ✅ 디버거 연결 감지
- ✅ 로그 제거

---

## 5. 코드 난독화

### ProGuard/R8 설정

**위치**: `app/proguard-rules.pro`

**주요 설정:**
```properties
-optimizationpasses 5              # 최적화 패스 횟수
-overloadaggressively              # 적극적 오버로딩
-repackageclasses ''               # 패키지 재구성
-allowaccessmodification           # 접근 제어자 수정 허용
```

### 난독화 검증

```bash
# Release APK 빌드
./gradlew assembleRelease

# 매핑 파일 확인
ls app/build/outputs/mapping/release/

# APK 분석
./gradlew :app:assembleRelease && \
  unzip -l app/build/outputs/apk/release/app-release.apk | grep "classes.dex"
```

---

## 6. 앱 무결성 검증

### Google Play Integrity API

**위치**: `app/src/main/java/com/example/grensilvideolist/security/PlayIntegrityChecker.kt`

**설정 단계:**

1. **Google Play Console 설정**
   - Play Console → Release → App Integrity
   - Integrity API 활성화

2. **Cloud Console 설정**
   ```bash
   # Google Cloud Console에서:
   # 1. 프로젝트 선택
   # 2. Play Integrity API 활성화
   # 3. 프로젝트 번호 확인
   ```

3. **코드에 프로젝트 번호 입력**
   ```kotlin
   // PlayIntegrityChecker.kt
   private const val CLOUD_PROJECT_NUMBER = YOUR_PROJECT_NUMBER
   ```

4. **서버 측 검증 구현** (필수)
   - 클라이언트에서 받은 토큰을 서버로 전송
   - 서버에서 Google API로 검증
   - 검증 결과에 따라 API 접근 제어

**검증 항목:**
- ✅ 앱 라이센스 (Play Store 설치)
- ✅ 앱 무결성 (변조 여부)
- ✅ 디바이스 무결성 (루팅, 에뮬레이터)

---

## 7. 배포 전 체크리스트

### 필수 설정

#### 네트워크
- [ ] Certificate Pinning 실제 핀 값 설정
- [ ] HTTP 트래픽 차단 확인
- [ ] API 도메인 모두 추가

#### 민감정보
- [ ] API 키 난독화 적용
- [ ] local.properties Git 제외 확인
- [ ] BuildConfig에 민감정보 없는지 확인

#### 데이터 보안
- [ ] EncryptedSharedPreferences 사용
- [ ] Room 데이터베이스 암호화 적용
- [ ] 백업 비활성화 (`allowBackup="false"`)

#### 런타임 보안
- [ ] Root 탐지 활성화
- [ ] 화면 보안 적용 (FLAG_SECURE)
- [ ] 디버깅 방지 (`isDebuggable = false`)

#### 코드 난독화
- [ ] ProGuard/R8 활성화 (`isMinifyEnabled = true`)
- [ ] 리소스 압축 (`isShrinkResources = true`)
- [ ] 보안 클래스 난독화 규칙 확인
- [ ] 매핑 파일 보관 (크래시 분석용)

#### 앱 서명
- [ ] Release keystore 생성
- [ ] 서명 정보 환경변수로 관리
- [ ] V1/V2/V3/V4 서명 활성화
- [ ] Keystore 백업 및 안전한 보관

#### Play Integrity
- [ ] Play Integrity API 활성화
- [ ] 프로젝트 번호 설정
- [ ] 서버 측 검증 구현

### 권장 설정

- [ ] 생체 인증 (지문, 얼굴 인식)
- [ ] 세션 타임아웃
- [ ] 자동 로그아웃
- [ ] SSL Pinning 우회 탐지
- [ ] 탈옥/루팅 탐지 강화
- [ ] 스크린 리더 악용 방지
- [ ] 클립보드 보안

### APK 분석 및 테스트

```bash
# 1. APK 빌드
./gradlew assembleRelease

# 2. APK 서명 확인
keytool -printcert -jarfile app/build/outputs/apk/release/app-release.apk

# 3. 난독화 확인
# Android Studio → Build → Analyze APK

# 4. 보안 테스트
# - 루팅된 기기에서 실행
# - 프록시 도구로 네트워크 감청 시도
# - APK 디컴파일 후 민감정보 확인
```

---

## 📚 추가 리소스

### 공식 문서
- [Android Security Best Practices](https://developer.android.com/topic/security/best-practices)
- [Network Security Configuration](https://developer.android.com/training/articles/security-config)
- [Play Integrity API](https://developer.android.com/google/play/integrity)
- [Security with HTTPS and SSL](https://developer.android.com/training/articles/security-ssl)

### 보안 도구
- [MobSF](https://github.com/MobSF/Mobile-Security-Framework-MobSF) - 모바일 보안 분석
- [OWASP Mobile Security Testing Guide](https://owasp.org/www-project-mobile-security-testing-guide/)
- [Android Backup Extractor](https://github.com/nelenkov/android-backup-extractor)

### 금융권 보안 가이드
- [금융보안원 모바일 금융서비스 보안 가이드](https://www.fsec.or.kr/)
- OWASP Mobile Top 10

---

## ⚠️ 주의사항

1. **절대 Git에 커밋하면 안 되는 파일:**
   - `local.properties`
   - `*.jks`, `*.keystore` (keystore 파일)
   - API 키가 포함된 설정 파일

2. **Release 빌드 전 확인:**
   - 디버그 로그 모두 제거
   - 테스트용 하드코딩된 값 제거
   - 프로덕션 API 엔드포인트 확인

3. **배포 후 모니터링:**
   - 크래시 리포트 (Firebase Crashlytics 권장)
   - 보안 이벤트 로깅
   - 이상 트래픽 탐지

---

## 📞 문의

보안 관련 이슈 발견 시 즉시 보고해주세요.
