@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.ktlint)
    // id("org.jetbrains.dokka") version "2.0.0"
}

group = "xyz.daaren"
version = "1.0.0"

kotlin {
    jvm()
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    wasmJs {
        browser()
        nodejs()
    }
    linuxX64()
    linuxArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                // put your multiplatform dependencies here
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = "xyz.daaren.chess.core"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()
    defaultConfig {
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

mavenPublishing {
    publishToMavenCentral()

    // Sign only when a key is configured, so publishToMavenLocal works without one.
    if (providers.gradleProperty("signingInMemoryKey").isPresent ||
        providers.gradleProperty("signing.keyId").isPresent
    ) {
        signAllPublications()
    }

    coordinates(group.toString(), "chess-core", version.toString())

    pom {
        name = "chess-core-kmp"
        description = "A Chess library."
        inceptionYear = "2025"
        url = "https://github.com/daarenx/chess-core-kmp/"
        licenses {
            license {
                name.set("The MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id = "DaarenX"
                name = "Alex Vogelsang"
                url = "https://github.com/daarenx"
            }
        }
        scm {
            url.set("https://github.com/daarenx/chess-core-kmp")
            connection.set("scm:git:git://github.com/daarenx/chess-core-kmp.git")
            developerConnection.set("scm:git:ssh://github.com:daarenx/chess-core-kmp.git")
        }
    }
}

ktlint {
    version.set(libs.versions.ktlint)
}

tasks.withType<Test>().configureEach {
    maxParallelForks = Runtime.getRuntime().availableProcessors()
    testLogging {
        events("passed", "failed")
    }
}

/*
dokka {
    moduleName.set("chess-core")
    dokkaPublications.html {
        suppressInheritedMembers.set(true)
        failOnWarning.set(true)
        outputDirectory.set(rootDir.resolve("build/dokka"))
    }

    dokkaSourceSets {
        configureEach {
            documentedVisibilities.set(
                setOf(
                    VisibilityModifier.Public,
                    VisibilityModifier.Internal,
                    VisibilityModifier.Protected,
                )
            )
        }

        named("commonMain") {
            includes.from("dokka/dokka.md")
        }
    }

    pluginsConfiguration.html {
        customStyleSheets.from("dokka/assets/custom.css")
        customAssets.from("dokka/assets/logo-icon.svg")
        footerMessage.set("chess-core")
    }

}
*/
