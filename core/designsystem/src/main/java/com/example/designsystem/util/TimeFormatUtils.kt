package com.example.designsystem.util

/**
 * 시간 포맷 유틸리티
 */
object TimeFormatUtils {

    /**
     * 초를 "M:SS" 형식으로 변환
     * @param seconds 총 초
     * @return "1:30" 또는 "0:45" 형식의 문자열
     */
    fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return "${minutes}:${secs.toString().padStart(2, '0')}"
    }

    /**
     * 밀리초를 "M:SS" 형식으로 변환
     * @param ms 밀리초
     * @return "1:30" 또는 "0:00" 형식의 문자열
     */
    fun formatTime(ms: Long): String {
        if (ms <= 0) return "0:00"
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "${minutes}:${seconds.toString().padStart(2, '0')}"
    }

    /**
     * 초를 "Xm Xs" 형식으로 변환 (상세 정보용)
     * @param seconds 총 초
     * @return "3m 45s" 또는 "45s" 형식의 문자열
     */
    fun formatDurationLong(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return if (minutes > 0) {
            "${minutes}m ${secs}s"
        } else {
            "${secs}s"
        }
    }
}
