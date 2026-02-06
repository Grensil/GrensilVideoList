package com.example.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.domain.model.Video
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoPlayerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var exoPlayer: ExoPlayer? = null
    private var currentVideoId: Long? = null
    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    // 현재 선택된 비디오 저장 (네비게이션용)
    private var _currentVideo: Video? = null
    val currentVideo: Video?
        get() = _currentVideo

    fun setCurrentVideo(video: Video) {
        _currentVideo = video
    }

    @OptIn(UnstableApi::class)
    fun getPlayer(): ExoPlayer {
        return exoPlayer ?: ExoPlayer.Builder(context)
            .build()
            .also { player ->
                exoPlayer = player
                setupPlayerListener(player)
            }
    }

    fun prepare(videoId: Long, url: String, muted: Boolean = true, autoPlay: Boolean = true) {
        val player = getPlayer()

        // 같은 비디오면 스킵
        if (currentVideoId == videoId && player.playbackState != Player.STATE_IDLE) {
            if (muted) player.volume = 0f else player.volume = 1f
            if (autoPlay && !player.isPlaying) player.play()
            return
        }

        currentVideoId = videoId
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.volume = if (muted) 0f else 1f
        player.playWhenReady = autoPlay
        player.prepare()

        _playbackState.value = _playbackState.value.copy(
            videoId = videoId,
            isFirstFrameRendered = false
        )
        startProgressTracking()
    }

    fun play() {
        exoPlayer?.play()
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
        // Seek 후 즉시 position 반영 (progress tracker 200ms 대기 없이)
        _playbackState.value = _playbackState.value.copy(
            currentPosition = positionMs
        )
    }

    fun setMuted(muted: Boolean) {
        exoPlayer?.volume = if (muted) 0f else 1f
    }

    fun getCurrentPosition(): Long {
        return exoPlayer?.currentPosition ?: 0L
    }

    fun getDuration(): Long {
        return exoPlayer?.duration ?: 0L
    }

    fun resetFirstFrame() {
        _playbackState.value = _playbackState.value.copy(isFirstFrameRendered = false)
    }

    fun stop() {
        progressJob?.cancel()
        exoPlayer?.stop()
        currentVideoId = null
        _playbackState.value = PlaybackState()
    }

    fun release() {
        progressJob?.cancel()
        exoPlayer?.release()
        exoPlayer = null
        currentVideoId = null
        _playbackState.value = PlaybackState()
    }

    private fun setupPlayerListener(player: ExoPlayer) {
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                val mappedState = when (state) {
                    Player.STATE_IDLE -> PlaybackState.STATE_IDLE
                    Player.STATE_BUFFERING -> PlaybackState.STATE_BUFFERING
                    Player.STATE_READY -> PlaybackState.STATE_READY
                    Player.STATE_ENDED -> PlaybackState.STATE_ENDED
                    else -> PlaybackState.STATE_IDLE
                }
                _playbackState.value = _playbackState.value.copy(
                    playbackState = mappedState,
                    duration = player.duration.coerceAtLeast(0L)
                )
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playbackState.value = _playbackState.value.copy(isPlaying = isPlaying)
            }

            override fun onRenderedFirstFrame() {
                _playbackState.value = _playbackState.value.copy(isFirstFrameRendered = true)
            }
        })
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                exoPlayer?.let { player ->
                    _playbackState.value = _playbackState.value.copy(
                        currentPosition = player.currentPosition.coerceAtLeast(0L),
                        duration = player.duration.coerceAtLeast(0L),
                        bufferedPercentage = player.bufferedPercentage
                    )
                }
                delay(200L) // 200ms마다 업데이트
            }
        }
    }
}
