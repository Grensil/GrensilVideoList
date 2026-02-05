package com.example.data.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 암호화된 SharedPreferences
 * 
 * 금융앱에서 민감한 데이터를 안전하게 저장:
 * - 사용자 토큰
 * - 인증 정보
 * - 개인 설정
 */
@Singleton
class SecurePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val encryptedPrefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * 암호화된 문자열 저장
     */
    fun putString(key: String, value: String) {
        encryptedPrefs.edit().putString(key, value).apply()
    }

    /**
     * 암호화된 문자열 읽기
     */
    fun getString(key: String, defaultValue: String? = null): String? {
        return encryptedPrefs.getString(key, defaultValue)
    }

    /**
     * 암호화된 Boolean 저장
     */
    fun putBoolean(key: String, value: Boolean) {
        encryptedPrefs.edit().putBoolean(key, value).apply()
    }

    /**
     * 암호화된 Boolean 읽기
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return encryptedPrefs.getBoolean(key, defaultValue)
    }

    /**
     * 암호화된 Int 저장
     */
    fun putInt(key: String, value: Int) {
        encryptedPrefs.edit().putInt(key, value).apply()
    }

    /**
     * 암호화된 Int 읽기
     */
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return encryptedPrefs.getInt(key, defaultValue)
    }

    /**
     * 암호화된 Long 저장
     */
    fun putLong(key: String, value: Long) {
        encryptedPrefs.edit().putLong(key, value).apply()
    }

    /**
     * 암호화된 Long 읽기
     */
    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return encryptedPrefs.getLong(key, defaultValue)
    }

    /**
     * 키 삭제
     */
    fun remove(key: String) {
        encryptedPrefs.edit().remove(key).apply()
    }

    /**
     * 모든 데이터 삭제
     */
    fun clear() {
        encryptedPrefs.edit().clear().apply()
    }

    /**
     * 키 존재 여부 확인
     */
    fun contains(key: String): Boolean {
        return encryptedPrefs.contains(key)
    }

    companion object {
        private const val PREFS_NAME = "secure_prefs"

        // 키 상수
        const val KEY_USER_TOKEN = "user_token"
        const val KEY_USER_ID = "user_id"
        const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    }
}
