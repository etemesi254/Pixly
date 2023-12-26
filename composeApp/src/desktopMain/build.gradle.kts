plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

compose.desktop {
    application {
        mainClass = "MainKt"
   //     val hostOs = System.getProperty("os.name")

//        // Set up dynamic libraries and their paths
//        // this sets up linux and windows stuff to be used for the places that
//        // needs them, without this we gonna have to have the dlls/so files in places the os
//        // can find
//        if (hostOs.contains("Linux")) {
//            jvmArgs("-Djava.library.path=" + file("${projectDir}/shared_libs/linux"))
//        } else if (hostOs.startsWith("Windows")) {
//            jvmArgs("-Djava.library.path=" + file("${projectDir}/shared_libs/windows"))
//        } else {
//            throw GradleException("No native library path for ${hostOs}, supported systems are windows and linux")
//        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.cae.pixly"
            packageVersion = "1.0.0"
        }
    }
}