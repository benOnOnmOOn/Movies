# The proguard configuration file for the following section is /Users/benedyktziobro/AndroidStudioProjects/Movies/app/build/intermediates/default_proguard_files/global/proguard-android-optimize.txt-8.0.2
# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html
#
# Starting with version 2.2 of the Android plugin for Gradle, this file is distributed together with
# the plugin and unpacked at build-time. The files in $ANDROID_HOME are no longer maintained and
# will be ignored by new version of the Android plugin for Gradle.

# Optimizations: If you don't want to optimize, use the proguard-android.txt configuration file
# instead of this one, which turns off the optimization flags.
# Adding optimization introduces certain risks, since for example not all optimizations performed by
# ProGuard works on all versions of Dalvik.  The following flags turn off various optimizations
# known to have issues, but the list may not be complete or up to date. (The "arithmetic"
# optimization can be used if you are only targeting Android 2.0 or later.)  Make sure you test
# thoroughly if you go this route.
-optimizations
-optimizationpasses 5
-allowaccessmodification

-dontusemixedcaseclassnames
-verbose


-keep class androidx.core.app.CoreComponentFactory { <init>(); }
-keep class androidx.profileinstaller.ProfileInstallReceiver { <init>(); }
-keep class androidx.room.MultiInstanceInvalidationService { <init>(); }
-keep class androidx.startup.InitializationProvider { <init>(); }
-keep class com.bz.movies.core.MoviesApp { <init>(); }
-keep class com.bz.movies.presentation.MainActivity { <init>(); }
-keep class com.google.android.datatransport.runtime.backends.TransportBackendDiscovery { <init>(); }
-keep class com.google.android.datatransport.runtime.scheduling.jobscheduling.AlarmManagerSchedulerBroadcastReceiver { <init>(); }
-keep class com.google.android.datatransport.runtime.scheduling.jobscheduling.JobInfoSchedulerService { <init>(); }
-keep class com.google.android.gms.measurement.AppMeasurementJobService { <init>(); }
-keep class com.google.android.gms.measurement.AppMeasurementReceiver { <init>(); }
-keep class com.google.android.gms.measurement.AppMeasurementService { <init>(); }
-keep class com.google.firebase.components.ComponentDiscoveryService { <init>(); }
-keep class com.google.firebase.provider.FirebaseInitProvider { <init>(); }

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Keep for the reflective cast done in EntryPoints.
# See b/183070411#comment4 for more info.
# Keep for the reflective cast done in EntryPoints.
-keep,allowobfuscation,allowshrinking @dagger.hilt.EntryPoint class *
# See b/183070411#comment4 for more info.
# Keep class names of Hilt injected ViewModels since their name are used as a multibinding map key.
-keep,allowobfuscation,allowshrinking @dagger.hilt.android.EarlyEntryPoint class *
# Keep for the reflective cast done in EntryPoints.
-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel
# See b/183070411#comment4 for more info.
-keep,allowobfuscation,allowshrinking @dagger.hilt.internal.ComponentEntryPoint class *
-keep,allowobfuscation,allowshrinking @dagger.hilt.internal.GeneratedEntryPoint class *


# Retain the @Navigator.Name annotation on each subclass of Navigator.
# R8 full mode only retains annotations on items matched by a -keep rule,
# hence the extra -keep rule for the subclasses of Navigator.
#
# A -keep rule for the Navigator.Name annotation class is not required
# since the annotation is referenced from the code.
-keepattributes RuntimeVisibleAnnotations
-keep,allowobfuscation,allowshrinking class * extends androidx.navigation.Navigator

# We keep all fields for every generated proto file as the runtime uses
# reflection over them that ProGuard cannot detect. Without this keep
# rule, fields may be removed that would cause runtime failures.
-keepclassmembers class * extends com.google.android.gms.internal.measurement.zzlb {
  <fields>;
}

# keep setters in VectorDrawables so that animations can still work.
-keepclassmembers class androidx.vectordrawable.graphics.drawable.VectorDrawableCompat$* {
   void set*(***);
   *** get*();
}

# this rule is need to work properly when app is compiled with api 28, see b/142778206
# Also this rule prevents registerIn from being inlined.
-keepclassmembers class androidx.lifecycle.ReportFragment$LifecycleCallbacks { *; }
-keepclassmembers,allowobfuscation class * extends androidx.lifecycle.ViewModel {
    <init>(androidx.lifecycle.SavedStateHandle);
}

# Keep Dynamite API entry points
-keep @interface com.google.android.gms.common.util.DynamiteApi
-keep @com.google.android.gms.common.util.DynamiteApi public class * {
  public <fields>;
  public <methods>;
}

-assumenosideeffects public class androidx.compose.runtime.ComposerKt {
    void sourceInformation(androidx.compose.runtime.Composer,java.lang.String);
    void sourceInformationMarkerStart(androidx.compose.runtime.Composer,int,java.lang.String);
    void sourceInformationMarkerEnd(androidx.compose.runtime.Composer);
}

# Allow R8 to optimize away the FastServiceLoader.
# Together with ServiceLoader optimization in R8
# this results in direct instantiation when loading Dispatchers.Main
-assumenosideeffects class kotlinx.coroutines.internal.MainDispatcherLoader {
    boolean FAST_SERVICE_LOADER_ENABLED return false;
}

-assumenosideeffects class kotlinx.coroutines.internal.FastServiceLoaderKt {
    boolean ANDROID_DETECTED return true;
}

# Disable support for "Missing Main Dispatcher", since we always have Android main dispatcher
-assumenosideeffects class kotlinx.coroutines.internal.MainDispatchersKt {
    boolean SUPPORT_MISSING return false;
}

# Statically turn off all debugging facilities and assertions
-assumenosideeffects class kotlinx.coroutines.DebugKt {
    boolean getASSERTIONS_ENABLED() return false;
    boolean getDEBUG() return false;
    boolean getRECOVER_STACK_TRACES() return false;
}

# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

-keep class * extends androidx.room.RoomDatabase

# It's important that we preserve initializer names, given they are used in the AndroidManifest.xml.
-keepnames class * extends androidx.startup.Initializer

# These Proguard rules ensures that ComponentInitializers are are neither shrunk nor obfuscated,
# and are a part of the primary dex file. This is because they are discovered and instantiated
# during application startup.
-keep class * extends androidx.startup.Initializer {
    # Keep the public no-argument constructor while allowing other methods to be optimized.
    <init>();
}

-assumenosideeffects class androidx.startup.StartupLogger { public static <methods>; }

-keep class * implements com.google.firebase.components.ComponentRegistrar
-keep,allowshrinking interface com.google.firebase.components.ComponentRegistrar

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Remove all standard Android logging invocations.
-assumenosideeffects class android.util.Log { *; }

-assumenosideeffects class java.util.logging.** { *; }

# Remove all printing of stack traces.
-assumenosideeffects class java.lang.Throwable {
    public void printStackTrace();
}

-repackageclasses ''
-allowaccessmodification

#Hide some missliding warnings
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
-dontwarn com.google.errorprone.annotations.**
-dontwarn java.lang.**
-dontwarn libcore.io.**
-dontwarn org.checkerframework.**
-dontwarn com.google.auto.value.**
-dontwarn org.codehaus.mojo.**