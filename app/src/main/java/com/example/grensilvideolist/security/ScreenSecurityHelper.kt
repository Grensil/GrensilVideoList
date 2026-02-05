package com.example.grensilvideolist.security

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * 화면 보안 헬퍼
 * 
 * 금융앱에서 필수적인 화면 보안 기능:
 * 1. 스크린샷 방지
 * 2. 화면 녹화 방지
 * 3. 최근 앱 목록에서 화면 블러 처리
 */
object ScreenSecurityHelper {

    /**
     * 스크린샷 및 화면 녹화 방지 활성화
     */
    fun enableScreenSecurity(activity: Activity) {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    /**
     * 스크린샷 및 화면 녹화 방지 비활성화
     */
    fun disableScreenSecurity(activity: Activity) {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}

/**
 * Compose에서 화면 보안 적용을 위한 Effect
 * 
 * 사용 예시:
 * @Composable
 * fun SecureScreen() {
 *     SecureScreenEffect()
 *     // 나머지 UI
 * }
 */
@Composable
fun SecureScreenEffect() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.let { ScreenSecurityHelper.enableScreenSecurity(it) }
        
        onDispose {
            activity?.let { ScreenSecurityHelper.disableScreenSecurity(it) }
        }
    }
}
