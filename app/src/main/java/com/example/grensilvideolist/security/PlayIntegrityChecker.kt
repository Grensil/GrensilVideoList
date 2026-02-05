package com.example.grensilvideolist.security

import android.content.Context
import com.google.android.play.core.integrity.IntegrityManager
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import com.google.android.play.core.integrity.IntegrityTokenResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Google Play Integrity API
 * 
 * 앱 무결성 검증:
 * 1. 앱이 Play Store에서 설치되었는지 확인
 * 2. 앱이 변조되지 않았는지 확인
 * 3. 디바이스가 안전한지 확인
 * 
 * 설정 방법:
 * 1. Google Play Console에서 Integrity API 활성화
 * 2. Cloud Console에서 API 키 생성
 * 3. 아래 CLOUD_PROJECT_NUMBER에 프로젝트 번호 입력
 */
@Singleton
class PlayIntegrityChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val integrityManager: IntegrityManager by lazy {
        IntegrityManagerFactory.create(context)
    }

    /**
     * 앱 무결성 검증
     *
     * @param nonce 서버에서 생성한 랜덤 nonce (보안을 위해 서버에서 생성 권장)
     * @return IntegrityTokenResponse 무결성 검증 토큰
     */
    suspend fun checkIntegrity(nonce: String): Result<IntegrityTokenResponse> {
        return try {
            val integrityTokenRequest = IntegrityTokenRequest.builder()
                .setNonce(nonce)
                .setCloudProjectNumber(CLOUD_PROJECT_NUMBER)
                .build()

            val response = suspendCancellableCoroutine<IntegrityTokenResponse> { continuation ->
                integrityManager.requestIntegrityToken(integrityTokenRequest)
                    .addOnSuccessListener { result ->
                        continuation.resume(result)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 무결성 토큰 검증 (서버에서 수행해야 함)
     * 
     * 클라이언트에서는 토큰을 서버로 전송하고,
     * 서버에서 Google Play Integrity API를 사용하여 검증해야 합니다.
     * 
     * 서버 검증 예시:
     * https://developer.android.com/google/play/integrity/verdict#decrypt-verify
     */
    suspend fun verifyIntegrityWithServer(token: String): Result<Boolean> {
        // TODO: 서버 API 호출하여 토큰 검증
        // POST /api/verify-integrity
        // Body: { "token": token }
        // Response: { "isValid": boolean, "verdict": {...} }
        
        return Result.success(true) // 임시 구현
    }

    companion object {
        // Google Cloud Console의 프로젝트 번호
        // TODO: 실제 프로젝트 번호로 교체
        private const val CLOUD_PROJECT_NUMBER = 0L
    }
}

/**
 * Integrity 검증 결과
 */
sealed class IntegrityResult {
    object Success : IntegrityResult()
    data class Failed(val reason: String) : IntegrityResult()
    data class Error(val exception: Exception) : IntegrityResult()
}
