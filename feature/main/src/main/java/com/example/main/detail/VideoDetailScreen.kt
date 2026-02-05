package com.example.main.detail

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.designsystem.theme.BookmarkActive
import com.example.designsystem.theme.DarkBackground
import com.example.designsystem.theme.DarkSurface
import com.example.designsystem.theme.Teal
import com.example.designsystem.util.TimeFormatUtils
import com.example.domain.model.Video

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(
    video: Video,
    viewModel: VideoDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val playbackState by viewModel.playbackState.collectAsState()
    val isBookmarked by viewModel.isBookmarked.collectAsState()
    val isFullscreen by viewModel.isFullscreen.collectAsState()

    // 화면 나갈 때 플레이어 숨김 (잔상 방지)
    var isExiting by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity

    // 비디오 설정
    LaunchedEffect(video) {
        viewModel.setVideo(video)
    }

    // 화면 방향 및 전체화면 처리
    DisposableEffect(isFullscreen) {
        if (isFullscreen) {
            activity?.let { act ->
                act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                WindowCompat.setDecorFitsSystemWindows(act.window, false)
                WindowInsetsControllerCompat(act.window, act.window.decorView).let { controller ->
                    controller.hide(WindowInsetsCompat.Type.systemBars())
                    controller.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
                act.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        } else {
            activity?.let { act ->
                // 디테일 화면에서는 세로 모드 고정
                act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                WindowCompat.setDecorFitsSystemWindows(act.window, true)
                WindowInsetsControllerCompat(act.window, act.window.decorView)
                    .show(WindowInsetsCompat.Type.systemBars())
                act.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
        onDispose {
            activity?.let { act ->
                // 화면 나갈 때 다시 자유롭게
                act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                WindowCompat.setDecorFitsSystemWindows(act.window, true)
                WindowInsetsControllerCompat(act.window, act.window.decorView)
                    .show(WindowInsetsCompat.Type.systemBars())
                act.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }

    // 뒤로가기 처리
    BackHandler {
        if (isFullscreen) {
            viewModel.exitFullscreen()
        } else {
            isExiting = true  // 플레이어 숨김 (잔상 방지)
            onBackClick()
        }
    }

    if (isFullscreen) {
        // 전체화면 모드
        VideoPlayerWithControls(
            exoPlayer = viewModel.playerManager.getPlayer(),
            playbackState = playbackState,
            isFullscreen = true,
            isExiting = isExiting,
            onPlayPauseClick = viewModel::togglePlayPause,
            onSeek = viewModel::seekTo,
            onFullscreenClick = viewModel::toggleFullscreen,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        // 일반 모드
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = {
                            isExiting = true  // 플레이어 숨김 (잔상 방지)
                            onBackClick()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = viewModel::toggleBookmark) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (isBookmarked) "Remove bookmark" else "Add bookmark",
                                tint = if (isBookmarked) BookmarkActive else Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkSurface
                    )
                )
            },
            containerColor = DarkBackground
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // 비디오 플레이어
                VideoPlayerWithControls(
                    exoPlayer = viewModel.playerManager.getPlayer(),
                    playbackState = playbackState,
                    isFullscreen = false,
                    isExiting = isExiting,
                    onPlayPauseClick = viewModel::togglePlayPause,
                    onSeek = viewModel::seekTo,
                    onFullscreenClick = viewModel::toggleFullscreen
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 비디오 정보
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    // 제작자 정보
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 프로필 이미지 placeholder
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Teal),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = video.user.name.firstOrNull()?.uppercase() ?: "U",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = video.user.name,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Creator",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 비디오 상세 정보
                    Text(
                        text = "Video Information",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    VideoInfoRow(label = "Duration", value = TimeFormatUtils.formatDurationLong(video.duration))
                    VideoInfoRow(label = "Resolution", value = "${video.width}x${video.height}")
                    VideoInfoRow(
                        label = "Quality",
                        value = video.videoFiles.maxByOrNull { it.width * it.height }?.quality ?: "Unknown"
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun VideoInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

