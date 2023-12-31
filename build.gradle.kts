// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.apply {
        set("agp_version", "7.3.0")
        set("compose_version", "1.4.2")
    }
}

plugins {
    id("com.android.application") version("7.4.1") apply false
    id("com.android.library") version("7.2.2") apply false
    id("org.jetbrains.kotlin.android") version("1.8.10") apply false
    id("com.google.dagger.hilt.android") version("2.44") apply false
//    id("com.google.gms.google-services") version("4.4.0") apply false
    id("com.google.firebase.crashlytics") version("2.9.4") apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}