package com.example.grensilvideolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.GrensilVideoListTheme
import com.example.grensilvideolist.security.ScreenSecurityHelper
import com.example.grensilvideolist.security.SecureScreenEffect
import com.example.main.player.VideoPlayerManager
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


@Composable
fun ErrorContent(message: String, onRetryClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Error: $message",
                color = MaterialTheme.colorScheme.error
            )
            androidx.compose.material3.Button(onClick = onRetryClick) {
                Text("Retry")
            }
        }
    }
}
