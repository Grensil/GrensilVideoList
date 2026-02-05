package com.example.network.security

import android.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API 키 보안 제공자
 * 
 * 금융앱 보안을 위해:
 * 1. API 키를 난독화하여 저장
 * 2. 런타임에 디코딩
 * 3. ProGuard/R8로 추가 난독화
 * 
 * 더 강력한 보안을 위해서는 NDK(C/C++)로 API 키를 저장하는 것을 권장
 */
@Singleton
class ApiKeyProvider @Inject constructor() {

    /**
     * API 키를 가져옵니다.
     * 
     * IMPORTANT: 실제 배포시에는 아래의 인코딩된 문자열을
     * 실제 API 키를 Base64로 인코딩한 값으로 교체해야 합니다.
     * 
     * 인코딩 방법:
     * echo -n "YOUR_API_KEY" | base64
     */
    fun getApiKey(): String {
        return try {
            // 실제 배포시 이 값을 교체하세요
            val encoded = "WU9VUl9BUElfS0VZX0hFUkU=" // "YOUR_API_KEY_HERE" encoded
            String(Base64.decode(encoded, Base64.DEFAULT))
        } catch (e: Exception) {
            // Fallback - 실제로는 BuildConfig에서 가져옴
            ""
        }
    }

    /**
     * 추가 XOR 난독화 (선택적)
     */
    private fun deobfuscate(obfuscated: ByteArray, key: Int): String {
        return String(obfuscated.map { (it.toInt() xor key).toByte() }.toByteArray())
    }

    companion object {
        // API 키를 난독화하여 저장하는 헬퍼 함수 (개발용)
        fun obfuscateKey(key: String): String {
            return Base64.encodeToString(key.toByteArray(), Base64.NO_WRAP)
        }
    }
}
