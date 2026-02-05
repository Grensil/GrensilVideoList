package com.example.player

data class PlaybackState(
    val videoId: Long? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val bufferedPercentage: Int = 0,
    val playbackState: Int = STATE_IDLE
) {
    val progress: Float
        get() = if (duration > 0) currentPosition.toFloat() / duration else 0f

    companion object {
        const val STATE_IDLE = 1
        const val STATE_BUFFERING = 2
        const val STATE_READY = 3
        const val STATE_ENDED = 4
    }
}
