package com.example.data.security

import android.content.Context
import androidx.room.RoomDatabase
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import androidx.sqlite.db.SupportSQLiteOpenHelper
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.io.File
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 데이터베이스 암호화 헬퍼
 * 
 * SQLCipher를 사용하여 Room 데이터베이스를 암호화합니다.
 * 금융앱에서 필수적인 데이터 보안
 */
@Singleton
class DatabaseEncryptionHelper @Inject constructor(
    private val context: Context
) {

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    /**
     * 암호화된 데이터베이스 팩토리 생성
     * 
     * Room Database Builder에서 사용:
     * Room.databaseBuilder(...)
     *     .openHelperFactory(databaseEncryptionHelper.getEncryptedFactory())
     *     .build()
     */
    fun getEncryptedFactory(): SupportSQLiteOpenHelper.Factory {
        val passphrase = getOrCreatePassphrase()
        return SupportFactory(passphrase)
    }

    /**
     * 데이터베이스 암호키 생성 또는 가져오기
     */
    private fun getOrCreatePassphrase(): ByteArray {
        val passphraseFile = File(context.filesDir, PASSPHRASE_FILE_NAME)
        
        return if (passphraseFile.exists()) {
            // 기존 암호키 읽기 (암호화된 파일에서)
            readEncryptedPassphrase(passphraseFile)
        } else {
            // 새로운 암호키 생성 및 저장
            val newPassphrase = generateSecurePassphrase()
            writeEncryptedPassphrase(passphraseFile, newPassphrase)
            newPassphrase
        }
    }

    /**
     * 보안 랜덤 암호키 생성
     */
    private fun generateSecurePassphrase(): ByteArray {
        val random = SecureRandom()
        val passphrase = ByteArray(32) // 256-bit
        random.nextBytes(passphrase)
        return passphrase
    }

    /**
     * 암호화된 파일에서 암호키 읽기
     */
    private fun readEncryptedPassphrase(file: File): ByteArray {
        val encryptedFile = EncryptedFile.Builder(
            context,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        return encryptedFile.openFileInput().use { it.readBytes() }
    }

    /**
     * 암호키를 암호화된 파일에 저장
     */
    private fun writeEncryptedPassphrase(file: File, passphrase: ByteArray) {
        val encryptedFile = EncryptedFile.Builder(
            context,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        encryptedFile.openFileOutput().use { it.write(passphrase) }
    }

    companion object {
        private const val PASSPHRASE_FILE_NAME = ".db_key"
        
        /**
         * SQLCipher 라이브러리 초기화
         */
        fun initializeSQLCipher(context: Context) {
            SQLiteDatabase.loadLibs(context)
        }
    }
}
