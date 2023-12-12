import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")

    google()

}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)

    // file picking
    implementation("com.darkrockstudios:mpfilepicker:2.1.0")
    // panes
    // https://mvnrepository.com/artifact/org.jetbrains.compose.components/components-splitpane-desktop
    implementation("org.jetbrains.compose.components:components-splitpane-desktop:1.5.0")
    // color picking

    implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")

    implementation("zil:pixly-image:0.4.0")

}
compose.desktop {
    application {
        mainClass = "MainKt"

        val hostOs = System.getProperty("os.name")

        // Set up dynamic libraties and their paths
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
            packageName = "Pixly"
            packageVersion = "1.0.0"
        }
    }
}
