plugins {
    alias(libs.plugins.movies.jvm.library)
    alias(libs.plugins.movies.kover)
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.binary.compatibility)
}

apiValidation {
    ignoredPackages.add("hilt_aggregated_deps")
    nonPublicMarkers.addAll(
        listOf(
            "dagger.internal.DaggerGenerated",
            "javax.annotation.processing.Generated",
            "dagger.hilt.codegen.OriginatingElement"
        )
    )
}

