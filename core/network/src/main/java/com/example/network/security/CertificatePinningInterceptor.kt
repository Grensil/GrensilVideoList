package com.example.network.security

import okhttp3.CertificatePinner
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Certificate Pinning 설정
 * 
 * 금융앱에서 필수적인 SSL Pinning 구현
 * 중간자 공격(MITM) 방지
 */
@Singleton
class CertificatePinningProvider @Inject constructor() {

    /**
     * Certificate Pinner 생성
     * 
     * 실제 인증서 핀을 얻는 방법:
     * echo | openssl s_client -connect api.pexels.com:443 2>/dev/null | \
     *   openssl x509 -pubkey -noout | \
     *   openssl pkey -pubin -outform der | \
     *   openssl dgst -sha256 -binary | base64
     * 
     * 또는 OkHttp의 CertificatePinner를 사용하여 런타임에 핀을 확인:
     * 1. 임시로 빈 CertificatePinner로 실행
     * 2. 로그에서 실제 핀 값 확인
     * 3. 확인된 핀 값을 아래에 추가
     */
    fun getCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
            // Pexels API의 실제 certificate pin을 추가하세요
            // 예시: .add("api.pexels.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
            // 예시: .add("api.pexels.com", "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=")
            
            // 백업 핀도 추가 (인증서 갱신 대비)
            .build()
    }

    /**
     * 디버그 모드에서는 Certificate Pinning을 비활성화할 수 있습니다.
     * (개발 환경에서 Charles, Fiddler 등 사용 시)
     */
    fun getCertificatePinnerForDebug(): CertificatePinner? {
        return null // 디버그에서는 pinning 비활성화
    }
}
