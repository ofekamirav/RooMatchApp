# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
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

# --- IMPORTANT: ADD THE FOLLOWING RULES FOR YOUR LIBRARIES ---

# Ktor Client rules
-dontwarn io.ktor.**
-keep class io.ktor.** { *; }
-keep interface io.ktor.** { *; }

# Kotlinx Serialization rules
-dontwarn kotlinx.serialization.**
-keep class kotlinx.serialization.** { *; }
-keepnames class * implements kotlinx.serialization.KSerializer
-keepclassmembers class * {
    @kotlinx.serialization.SerialName *;
}
# For sealed classes or polymorphic serialization
-keep class * implements kotlinx.serialization.SerializationStrategy
-keep class * implements kotlinx.serialization.DeserializationStrategy

# Google Play Services (including Google Sign-In, Firebase etc.)
# These rules are critical for preventing crashes with obfuscation
-keep class com.google.android.gms.** { *; }
-keep interface com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# For specific Google Sign-In components if general rules are insufficient
-keep class com.google.android.gms.auth.api.** { *; }
-keep class com.google.android.gms.tasks.** { *; }
-keep class com.google.android.gms.common.** { *; }

# For classes that implement Parcelable (used in Intents and Bundles)
# Google Play Services often uses Parcelable objects for results
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# General Kotlin rules (broader and safer than specific coroutines.jvm.internal)
-keep class kotlin.** { *; }
-dontwarn kotlin.**
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**


# Room Database rules (simplified and commonly used)
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**
-keep public class com.example.roomatchapp.** { *; }

# For Coil (image loading library)
-keepnames class * extends coil.request.ImageResult { *; }
-keepnames class * extends coil.request.ImageRequest { *; }
-keepnames class coil.disk.DiskCache$Builder { *; }
-keepnames class coil.memory.MemoryCache$Builder { *; }
-dontwarn coil.util.*

# For DataStore Preferences
-keep class androidx.datastore.preferences.core.** { *; }

# For Play Services Location (if you use it)
-keep class com.google.android.gms.location.** { *; }
-dontwarn com.google.android.gms.location.**

# For Google Maps Compose (if you use it)
-keep class com.google.maps.android.** { *; }
-dontwarn com.google.maps.android.**

# For Accompanist Pager (or other Accompanist libraries)
-keepnames class com.google.accompanist.** { *; }
-dontwarn com.google.accompanist.**

# For Vico (charting library)
-keepnames class com.patrykandpatrick.vico.** { *; }
-dontwarn com.patrykandpatrick.vico.**