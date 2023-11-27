import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
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
    implementation("io.ktor:ktor-client-cio:2.3.6")
    implementation ("com.aallam.openai:openai-client:3.6.0")

    // file picking
   implementation("com.darkrockstudios:mpfilepicker:2.1.0")
    // panes
    // https://mvnrepository.com/artifact/org.jetbrains.compose.components/components-splitpane-desktop
    implementation("org.jetbrains.compose.components:components-splitpane-desktop:1.5.0")




}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Pixly"
            packageVersion = "1.0.0"
        }
    }
}
