import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.compose

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
}
repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    mavenLocal()
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    
    jvm("desktop")

    
    sourceSets {
        val commonMain by getting;
        val desktopMain by getting{
//            dependsOn(commonMain)

        }
        
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.accompanist.permissions)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)

            // file picking
            implementation(libs.mpfilepicker)
            // panes


            // reorderable components
            implementation(libs.reorderable)

        }
        desktopMain.dependencies {
            implementation(libs.compose.color.picker.jvm)
            // https://mvnrepository.com/artifact/org.jetbrains.compose.components/components-splitpane-desktop
            implementation(libs.components.splitpane.desktop)
            implementation(compose.desktop.currentOs)
        }

    }

}

android {
    namespace = "org.cae.pixly"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "org.cae.pixly"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}


compose.desktop {
    application {
        mainClass = "MainKt"
        val hostOs = System.getProperty("os.name")

        // Set up dynamic libraries and their paths
        // this sets up linux and windows stuff to be used for the places that
        // needs them, without this we gonna have to have the dlls/so files in places the os
        // can find
        if (hostOs.contains("Linux")) {
            jvmArgs("-Djava.library.path=" + file("${projectDir}/shared_libs/linux"))
        } else if (hostOs.startsWith("Windows")) {
            jvmArgs("-Djava.library.path=" + file("${projectDir}/shared_libs/windows"))
        } else {
            throw GradleException("No native library path for ${hostOs}, supported systems are windows and linux")
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.cae.pixly"
            packageVersion = "1.0.0"
        }
    }
}