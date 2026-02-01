# Remove packe name from classes
-repackageclasses ''

# Remove all standard Android logging invocations.
-assumenosideeffects class android.util.Log { *; }
-assumenosideeffects class java.util.logging.** { *; }

# Remove all printing of stack traces.
-assumenosideeffects class java.lang.Throwable {
    public void printStackTrace();
}

-processkotlinnullchecks remove


# Valid rules copy paste from file "proguard-android-optimize.txt"

# For native methods, see https://www.guardsquare.com/manual/configuration/examples#native
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

# For enumeration classes, see https://www.guardsquare.com/manual/configuration/examples#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-dontwarn androidx.vectordrawable.graphics.drawable.**
-dontwarn kotlinx.serialization.**
-dontwarn androidx.emoji2.**
-dontwarn androidx.window.extensions.**
