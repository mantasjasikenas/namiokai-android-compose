# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel

# For Google Play Services
#-keepnames class com.google.android.gms.** {*;}


-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses


-keepclassmembers class com.github.mantasjasikenas.namiokai.model.** {
    *;
}

-keepclassmembers class com.github.mantasjasikenas.core.domain.model.bills.** {
    *;
}

-keepclassmembers class com.github.mantasjasikenas.core.domain.model.** {
    *;
}

-keep class * {
 @kotlinx.serialization.SerialName <fields>;
}

-keepnames class com.path.to.your.ParcelableArg
-keepnames class com.path.to.your.SerializableArg
-keepnames class com.path.to.your.EnumArg

-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

-keep class androidx.compose.ui.platform.AndroidCompositionLocals_androidKt { *; }