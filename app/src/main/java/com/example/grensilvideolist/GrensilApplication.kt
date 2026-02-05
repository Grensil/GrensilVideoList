package com.example.grensilvideolist

import android.app.Application
import android.util.Log
import com.example.data.security.DatabaseEncryptionHelper
import com.example.grensilvideolist.security.SecurityChecker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GrensilApplication : Application() {

    @Inject
    lateinit var securityChecker: SecurityChecker

    override fun onCreate() {
        super.onCreate()

        // SQLCipher 초기화 (Room 암호화를 위해)
        try {
            DatabaseEncryptionHelper.initializeSQLCipher(this)
        } catch (e: Exception) {
            Log.e(TAG, "SQLCipher initialization failed", e)
            // 개발 중에는 에러를 무시하고 계속 진행
            // 실제 배포시에는 암호화된 DB를 사용해야 함
        }

        // 금융앱 보안 검사 (Release 빌드에서만)
        if (!BuildConfig.DEBUG) {
            performSecurityChecks()
        }
    }

    /**
     * 보안 검사 수행
     * 금융앱에서는 루팅, 디버깅, 에뮬레이터 등을 체크해야 합니다.
     */
    private fun performSecurityChecks() {
        val securityResult = securityChecker.performSecurityCheck()

        if (!securityResult.isSecure) {
            // 보안 위협 감지 시 처리
            Log.e(TAG, "Security threat detected: ${securityResult.getWarningMessage()}")

            // 옵션 1: 앱 종료
            // exitProcess(0)

            // 옵션 2: 경고 메시지 표시 (사용자에게 선택권 부여)
            // 실제 금융앱에서는 앱을 종료하거나 제한된 기능만 제공해야 합니다.

            // 옵션 3: 서버에 보안 이벤트 로깅
            // reportSecurityEvent(securityResult)
        }
    }

    companion object {
        private const val TAG = "GrensilApplication"
    }
}
