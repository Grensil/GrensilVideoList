package com.example.grensilvideolist.security

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 금융앱 보안 검사 도구
 *
 * 주요 기능:
 * 1. Root 탐지
 * 2. 디버깅 방지
 * 3. 에뮬레이터 탐지
 * 4. 앱 무결성 검증
 */
@Singleton
class SecurityChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * 루팅 여부 확인
     */
    fun isDeviceRooted(): Boolean {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3()
    }

    /**
     * Root 탐지 방법 1: su 바이너리 확인
     */
    private fun checkRootMethod1(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )
        return paths.any { File(it).exists() }
    }

    /**
     * Root 탐지 방법 2: BUILD 태그 확인
     */
    private fun checkRootMethod2(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    /**
     * Root 탐지 방법 3: 루팅 관련 앱 확인
     */
    private fun checkRootMethod3(): Boolean {
        val packages = arrayOf(
            "com.noshufou.android.su",
            "com.noshufou.android.su.elite",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.thirdparty.superuser",
            "com.yellowes.su",
            "com.topjohnwu.magisk"
        )
        return packages.any { isPackageInstalled(it) }
    }

    /**
     * 디버깅 모드 확인
     */
    fun isDebuggable(): Boolean {
        return (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }

    /**
     * 에뮬레이터 여부 확인
     */
    fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || "google_sdk" == Build.PRODUCT)
    }

    /**
     * 패키지 설치 여부 확인
     */
    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 전체 보안 검사
     */
    fun performSecurityCheck(): SecurityCheckResult {
        return SecurityCheckResult(
            isRooted = isDeviceRooted(),
            isDebuggable = isDebuggable(),
            isEmulator = isEmulator()
        )
    }
}

/**
 * 보안 검사 결과
 */
data class SecurityCheckResult(
    val isRooted: Boolean,
    val isDebuggable: Boolean,
    val isEmulator: Boolean
) {
    val isSecure: Boolean
        get() = !isRooted && !isDebuggable && !isEmulator

    fun getWarningMessage(): String? {
        return when {
            isRooted -> "루팅된 기기에서는 보안상 앱을 실행할 수 없습니다."
            isDebuggable -> "디버그 모드에서는 앱을 실행할 수 없습니다."
            isEmulator -> "에뮬레이터에서는 보안상 앱을 실행할 수 없습니다."
            else -> null
        }
    }
}
