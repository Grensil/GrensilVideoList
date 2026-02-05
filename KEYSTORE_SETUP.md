# Keystore 생성 및 서명 설정 가이드

## 1. Keystore 생성

### 명령어
```bash
keytool -genkey -v \
  -keystore release-keystore.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias release-key
```

### 입력 정보
- Password: 강력한 비밀번호 사용
- Name, Organization: 실제 정보 입력
- Alias: release-key (또는 원하는 이름)

### 중요!
- ⚠️ **Keystore 파일과 비밀번호를 절대 잃어버리지 마세요!**
- ⚠️ **Git에 커밋하지 마세요!**
- ⚠️ **안전한 곳에 백업하세요!**

---

## 2. 환경변수 설정

### macOS/Linux
```bash
# ~/.zshrc 또는 ~/.bashrc에 추가
export KEYSTORE_FILE="/path/to/release-keystore.jks"
export KEYSTORE_PASSWORD="your_keystore_password"
export KEY_ALIAS="release-key"
export KEY_PASSWORD="your_key_password"
```

### Windows
```powershell
# 환경변수 설정
setx KEYSTORE_FILE "C:\path\to\release-keystore.jks"
setx KEYSTORE_PASSWORD "your_keystore_password"
setx KEY_ALIAS "release-key"
setx KEY_PASSWORD "your_key_password"
```

---

## 3. Gradle 서명 설정

`app/build.gradle.kts`의 주석을 해제하고 수정:

```kotlin
signingConfigs {
    create("release") {
        storeFile = file(System.getenv("KEYSTORE_FILE") ?: "../release-keystore.jks")
        storePassword = System.getenv("KEYSTORE_PASSWORD")
        keyAlias = System.getenv("KEY_ALIAS")
        keyPassword = System.getenv("KEY_PASSWORD")
        
        enableV1Signing = true
        enableV2Signing = true
        enableV3Signing = true
        enableV4Signing = true
    }
}

buildTypes {
    release {
        signingConfig = signingConfigs.getByName("release")
        // ... 나머지 설정
    }
}
```

---

## 4. CI/CD 설정

### GitHub Actions 예시

```yaml
name: Build Release APK

on:
  push:
    tags:
      - 'v*'

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK
        uses: actions/setup-java@v3
        with:
          java-version: '11'
          
      - name: Decode Keystore
        run: |
          echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > release-keystore.jks
          
      - name: Build Release APK
        env:
          KEYSTORE_FILE: release-keystore.jks
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
        run: ./gradlew assembleRelease
```

### Secrets 등록 (GitHub)
1. Keystore 파일을 Base64로 인코딩:
   ```bash
   base64 -i release-keystore.jks | pbcopy  # macOS
   base64 release-keystore.jks | clip       # Windows
   ```

2. GitHub Repository → Settings → Secrets에 추가:
   - `KEYSTORE_BASE64`: Base64 인코딩된 keystore
   - `KEYSTORE_PASSWORD`: Keystore 비밀번호
   - `KEY_ALIAS`: Key alias
   - `KEY_PASSWORD`: Key 비밀번호

---

## 5. 서명 검증

### APK 서명 확인
```bash
keytool -printcert -jarfile app/build/outputs/apk/release/app-release.apk
```

### 출력 예시
```
Signer #1:

Signature:

Owner: CN=Your Name, OU=Your Organization, O=Your Company, L=City, ST=State, C=KR
Issuer: CN=Your Name, OU=Your Organization, O=Your Company, L=City, ST=State, C=KR
Serial number: 12345678
Valid from: Mon Jan 01 00:00:00 KST 2024 until: Thu Jan 01 00:00:00 KST 2054
Certificate fingerprints:
     SHA1: XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX
     SHA256: XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX
```

---

## 6. Play Store 업로드

### App Bundle 생성 (권장)
```bash
./gradlew bundleRelease
```

### 출력 위치
- APK: `app/build/outputs/apk/release/app-release.apk`
- AAB: `app/build/outputs/bundle/release/app-release.aab`

### Play Console 업로드
1. [Google Play Console](https://play.google.com/console) 로그인
2. 앱 선택 → Release → Production
3. AAB 파일 업로드
4. Release Notes 작성
5. Review 제출

---

## 7. 보안 체크리스트

- [x] Keystore 파일 안전하게 보관
- [x] Keystore 비밀번호 문서화 (안전한 곳에)
- [x] `.gitignore`에 `*.jks`, `*.keystore` 추가
- [x] 환경변수로 민감정보 관리
- [x] CI/CD Secrets 설정
- [x] V1/V2/V3/V4 서명 모두 활성화
- [x] 서명 검증 완료

---

## 8. 문제 해결

### Keystore를 잃어버렸을 때
- ❌ **복구 불가능!**
- 새로운 패키지명으로 새 앱 등록 필요
- 기존 사용자는 앱 업데이트 불가

### 비밀번호를 잊었을 때
- ❌ **복구 불가능!**
- 위와 동일한 상황

### 예방책
1. **여러 곳에 백업**
   - 안전한 클라우드 스토리지
   - 팀 공유 저장소 (암호화)
   - 물리적 USB

2. **비밀번호 관리**
   - 비밀번호 관리 도구 사용
   - 팀원과 안전하게 공유

3. **문서화**
   - Keystore 정보 문서 작성
   - 복구 절차 문서화
