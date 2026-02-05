package com.example.grensilvideolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.designsystem.theme.GrensilVideoListTheme
import com.example.grensilvideolist.security.ScreenSecurityHelper
import com.example.grensilvideolist.security.SecureScreenEffect
import com.example.player.VideoPlayerManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var videoPlayerManager: VideoPlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 금융앱 화면 보안 설정 (스크린샷, 화면 녹화 방지)
        if (!BuildConfig.DEBUG) {
            ScreenSecurityHelper.enableScreenSecurity(this)
        }

        enableEdgeToEdge()
        setContent {
            GrensilVideoListTheme {
                // 화면 보안 적용 (Compose)
                if (!BuildConfig.DEBUG) {
                    SecureScreenEffect()
                }

                MainScreen(videoPlayerManager = videoPlayerManager)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 화면 보안 해제
        if (!BuildConfig.DEBUG) {
            ScreenSecurityHelper.disableScreenSecurity(this)
        }
    }
}
