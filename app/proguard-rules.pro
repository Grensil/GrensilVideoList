# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep line numbers for debugging crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep generic signature for Kotlin (for reflection)
-keepattributes Signature
-keepattributes *Annotation*

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep data models
-keep class com.example.domain.model.** { *; }
-keep class com.example.data.model.** { *; }
-keep class com.example.network.model.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper
-keepclassmembers class * {
    @javax.inject.* <methods>;
    @javax.inject.* <fields>;
    @dagger.* <methods>;
    @dagger.* <fields>;
}

# Jetpack Compose
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# ==========================================
# 금융앱 보안 강화 설정
# ==========================================

# 1. 난독화 강화 옵션
-optimizationpasses 5
-overloadaggressively
-repackageclasses ''
-allowaccessmodification

# 2. 보안 클래스 난독화 (이름만 난독화, 기능은 유지)
-keepclassmembers class com.example.grensilvideolist.security.** { *; }
-keepclassmembers class com.example.data.security.** { *; }
-keepclassmembers class com.example.network.security.** { *; }

# 3. 스택 트레이스 정보 유지 (크래시 분석용)
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# 4. Native 메서드 보호 (NDK 사용시)
-keepclasseswithmembernames class * {
    native <methods>;
}

# 5. 열거형 보호
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 6. Serializable 클래스 보호
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 7. 보안 관련 라이브러리
# Security Crypto
-keep class androidx.security.crypto.** { *; }
-keepclassmembers class androidx.security.crypto.** { *; }

# SQLCipher
-keep class net.sqlcipher.** { *; }
-keepclassmembers class net.sqlcipher.** { *; }

# Play Integrity
-keep class com.google.android.play.core.integrity.** { *; }
-keepclassmembers class com.google.android.play.core.integrity.** { *; }

# 8. 리플렉션 사용 클래스 보호
-keepattributes *Annotation*,Signature,Exception,InnerClasses,EnclosingMethod

# 9. 최적화에서 제외할 패턴
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

# 10. 크래시 리포팅을 위한 라인 번호 유지
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# 11. 보안 강화: 문자열 암호화 (R8 자동)
# R8은 자동으로 일부 문자열을 난독화합니다

# 12. 디버그 정보 제거
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# 13. BuildConfig 난독화
-assumenosideeffects class com.example.grensilvideolist.BuildConfig {
    public static final boolean DEBUG return false;
}