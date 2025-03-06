import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("com.apollographql.apollo") version "4.1.1"
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation("com.apollographql.apollo:apollo-runtime:4.1.1")
            implementation("io.coil-kt.coil3:coil-compose:3.1.0")
            implementation("io.coil-kt.coil3:coil-network-okhttp:3.1.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
            implementation("com.typesafe:config:1.4.1")
            implementation("com.moandjiezana.toml:toml4j:0.7.2")
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

apollo {
    service("service") {
        packageName.set("dev.dubsky.aiko.graphql")
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "dev.dubsky.aiko.resources"
    generateResClass = auto
}

compose.desktop {
    application {
        mainClass = "dev.dubsky.aiko.EntryKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "dev.dubsky.aiko"
            packageVersion = "1.4.0"
        }
    }
}
