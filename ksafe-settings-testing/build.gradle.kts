import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.vanniktechPublish)
    signing
}

group = "io.github.ashrafkhan19"
version = "0.1.0-alpha"

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "KSafeSettingsTesting"
            isStatic = true
        }
    }

    jvm()

    js { browser() }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { browser() }

    androidLibrary {
        namespace = "io.github.ashrafkhan19.ksafesettings.testing"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.ksafeSettingsCore)
            implementation(libs.kotlinx.coroutinesCore)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutinesCore)
            implementation(libs.kotlinx.coroutinesTest)
        }
    }
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)

    coordinates(
        groupId = "io.github.ashrafkhan19",
        artifactId = "ksafe-settings-testing",
        version = "0.1.0-alpha",
    )

    pom {
        name = "KSafeSettings Testing"
        description = "In-memory fake implementation of KSafeSettings for unit tests."
        inceptionYear = "2026"
        url = "https://github.com/Ashrafkhan19/KSafeSettings"

        licenses {
            license {
                name = "Apache License 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0"
            }
        }

        developers {
            developer {
                id = "ashrafkhan19"
                name = "Ashraf Khan"
                url = "https://github.com/Ashrafkhan19"
            }
        }

        scm {
            url = "https://github.com/Ashrafkhan19/KSafeSettings"
            connection = "scm:git:git://github.com/Ashrafkhan19/KSafeSettings.git"
            developerConnection = "scm:git:ssh://git@github.com/Ashrafkhan19/KSafeSettings.git"
        }
    }
}

signing {
    val keyId = providers.gradleProperty("signingInMemoryKeyId").orNull
    val key = providers.gradleProperty("signingInMemoryKey").orNull
    val password = providers.gradleProperty("signingInMemoryKeyPassword").orNull
    if (key != null) {
        useInMemoryPgpKeys(keyId, key, password)
        sign(publishing.publications)
    }
}
