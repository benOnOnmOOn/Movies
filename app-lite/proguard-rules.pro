# Remove packe name from classes
-repackageclasses ''

# Remove all standard Android logging invocations.
-assumenosideeffects class android.util.Log { *; }
-assumenosideeffects class java.util.logging.** { *; }

# Remove all printing of stack traces.
-assumenosideeffects class java.lang.Throwable {
    public void printStackTrace();
}

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
  public static void checkExpressionValueIsNotNull(java.lang.Object, java.lang.String);
  public static void checkFieldIsNotNull(java.lang.Object, java.lang.String);
  public static void checkFieldIsNotNull(java.lang.Object, java.lang.String, java.lang.String);
  public static void checkNotNull(java.lang.Object);
  public static void checkNotNull(java.lang.Object, java.lang.String);
  public static void checkNotNullExpressionValue(java.lang.Object, java.lang.String);
  public static void checkNotNullParameter(java.lang.Object, java.lang.String);
  public static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
  public static void checkReturnedValueIsNotNull(java.lang.Object, java.lang.String);
  public static void checkReturnedValueIsNotNull(java.lang.Object, java.lang.String, java.lang.String);
  public static void throwUninitializedPropertyAccessException(java.lang.String);
}

-whyareyoukeeping class androidx.compose.ui.**

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
