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

# Remove all standard Android logging invocations.
-assumenosideeffects class android.util.Log { *; }
-assumenosideeffects class java.util.logging.** { *; }

# Remove all printing of stack traces.
-assumenosideeffects class java.lang.Throwable {
    public void printStackTrace();
}

-repackageclasses ''
-allowaccessmodification
-whyareyoukeeping

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
-dontwarn javax.annotation.**