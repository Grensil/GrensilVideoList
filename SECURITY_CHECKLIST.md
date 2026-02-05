# 🔒 금융앱 보안 체크리스트

Play Store 배포 전 필수 확인 사항

## 1️⃣ 네트워크 보안

### Certificate Pinning
- [ ] `network_security_config.xml` 생성 완료
- [ ] 실제 API 도메인 인증서 핀 추출 및 설정
- [ ] 백업 핀 추가 (인증서 갱신 대비)
- [ ] Pin 만료일 설정
- [ ] HTTP 트래픽 차단 (`cleartextTrafficPermitted="false"`)

### 검증 방법
```bash
# 인증서 핀 추출
echo | openssl s_client -connect your-api.com:443 2>/dev/null | \
  openssl x509 -pubkey -noout | \
  openssl pkey -pubin -outform der | \
  openssl dgst -sha256 -binary | base64
```

---

## 2️⃣ API 키 및 민감정보

### BuildConfig 보안
- [ ] API 키를 `local.properties`에서 로드
- [ ] `local.properties`가 `.gitignore`에 포함됨
- [ ] Git 히스토리에 API 키 없음 확인
- [ ] Release 빌드에서 API 키 난독화

### 환경변수 관리
- [ ] Keystore 정보 환경변수로 관리
- [ ] CI/CD에 시크릿 설정

---

## 3️⃣ 데이터 암호화

### SharedPreferences
- [ ] `EncryptedSharedPreferences` 사용
- [ ] MasterKey AES256_GCM 설정
- [ ] 민감 데이터 (토큰, 개인정보) 암호화 저장

### Room Database
- [ ] SQLCipher 라이브러리 추가
- [ ] Database 암호화 팩토리 적용
- [ ] 암호키 안전하게 저장 (EncryptedFile)
- [ ] Application에서 SQLCipher 초기화

---

## 4️⃣ 런타임 보안

### Root 탐지
- [ ] `SecurityChecker` 구현
- [ ] su 바이너리 확인
- [ ] 루팅 앱 탐지 (SuperSU, Magisk 등)
- [ ] test-keys 빌드 태그 확인
- [ ] 루팅 감지 시 앱 종료 또는 제한 모드

### 화면 보안
- [ ] `FLAG_SECURE` 설정 (스크린샷 방지)
- [ ] Release 빌드에서만 적용
- [ ] MainActivity에 적용 완료

### 디버깅 방지
- [ ] Release: `isDebuggable = false`
- [ ] 디버거 연결 탐지
- [ ] 에뮬레이터 탐지

---

## 5️⃣ 코드 난독화

### ProGuard/R8
- [ ] `isMinifyEnabled = true` (Release)
- [ ] `isShrinkResources = true`
- [ ] ProGuard 규칙 설정
  - [ ] 난독화 강화 옵션
  - [ ] 보안 클래스 보호
  - [ ] 디버그 로그 제거
- [ ] 매핑 파일 보관 (`mapping.txt`)

### 검증
- [ ] Release APK 빌드 성공
- [ ] Analyze APK로 클래스 난독화 확인
- [ ] APK 디컴파일 후 민감정보 검사

```bash
# APK 분석
./gradlew assembleRelease
# Android Studio → Build → Analyze APK
```

---

## 6️⃣ 앱 서명

### Keystore 생성
- [ ] Release keystore 생성
- [ ] 안전한 위치에 백업
- [ ] Keystore 정보 문서화

```bash
# Keystore 생성
keytool -genkey -v -keystore release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias release-key
```

### 서명 설정
- [ ] `signingConfigs` 설정
- [ ] V1/V2/V3/V4 서명 모두 활성화
- [ ] 환경변수로 keystore 정보 주입

### 검증
```bash
# 서명 확인
keytool -printcert -jarfile app-release.apk
```

---

## 7️⃣ 앱 무결성 검증

### Google Play Integrity API
- [ ] Play Console에서 Integrity API 활성화
- [ ] Google Cloud Console에서 API 활성화
- [ ] 프로젝트 번호 설정
- [ ] `PlayIntegrityChecker` 구현
- [ ] 서버 측 검증 API 구현 (필수!)

---

## 8️⃣ AndroidManifest 보안

### 필수 설정
- [ ] `android:allowBackup="false"`
- [ ] `android:usesCleartextTraffic="false"`
- [ ] `android:networkSecurityConfig` 설정
- [ ] 불필요한 권한 제거
- [ ] `exported` 속성 명시

---

## 9️⃣ 빌드 설정

### Release Build
- [ ] `isDebuggable = false`
- [ ] `isMinifyEnabled = true`
- [ ] `isShrinkResources = true`
- [ ] `buildConfigField` 보안 설정
- [ ] NDK `debugSymbolLevel = FULL`

### BuildConfig
- [ ] `DEBUG_MODE = false`
- [ ] `ENABLE_LOGGING = false`
- [ ] API 키 난독화

---

## 🔟 보안 테스트

### 정적 분석
- [ ] APK 디컴파일 후 민감정보 확인
- [ ] 하드코딩된 API 키 검사
- [ ] 로그 출력 확인

### 동적 분석
- [ ] 루팅된 기기에서 실행 테스트
- [ ] Frida/Xposed로 후킹 시도
- [ ] Charles/Burp Suite로 네트워크 감청 시도
- [ ] 스크린샷 차단 확인
- [ ] 백업 추출 시도

### 도구
```bash
# MobSF로 보안 분석
docker run -it -p 8000:8000 opensecurity/mobile-security-framework-mobsf
```

---

## 1️⃣1️⃣ Play Store 배포

### 앱 번들 생성
- [ ] AAB (Android App Bundle) 빌드
- [ ] 서명 완료
- [ ] 용량 최적화 확인

```bash
./gradlew bundleRelease
```

### Play Console 설정
- [ ] 앱 서명 키 등록
- [ ] 개인정보 처리방침 URL
- [ ] 데이터 보안 설문 작성
- [ ] 앱 콘텐츠 등급
- [ ] 타겟 국가/지역 설정

---

## 1️⃣2️⃣ 금융권 추가 요구사항

### 필수 구현
- [ ] 생체 인증 (지문, 얼굴 인식)
- [ ] 세션 타임아웃 (5-15분)
- [ ] 자동 로그아웃
- [ ] 거래 재인증
- [ ] 금액 입력 시 보안 키패드

### 권장 구현
- [ ] 화면 캡처 이벤트 감지
- [ ] 클립보드 보안 (자동 삭제)
- [ ] 화면 녹화 감지
- [ ] VPN 사용 감지
- [ ] 접근성 서비스 악용 방지

---

## 📋 최종 점검

### 배포 직전
- [ ] 모든 체크리스트 항목 완료
- [ ] QA 팀 보안 테스트 완료
- [ ] 침투 테스트 완료
- [ ] 버그 수정 및 재테스트
- [ ] Release Notes 작성

### 배포 후
- [ ] 크래시 리포트 모니터링
- [ ] 보안 이벤트 로깅
- [ ] 이상 트래픽 탐지
- [ ] 사용자 피드백 확인

---

## ✅ 체크리스트 완료 기준

**모든 필수 항목 ([ ])이 체크되어야 Play Store 배포 가능**

- 네트워크 보안: 100%
- 데이터 암호화: 100%
- 코드 난독화: 100%
- 앱 서명: 100%
- 런타임 보안: 100%

---

## 🚨 긴급 보안 이슈 대응

1. **즉시 업데이트 배포**
2. **영향 받는 사용자 식별**
3. **서버 측 추가 검증**
4. **보안 공지**
5. **사후 분석 및 재발 방지**
