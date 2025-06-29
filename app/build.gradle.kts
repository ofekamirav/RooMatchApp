import org.gradle.kotlin.dsl.implementation
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    kotlin("kapt")
}
val keystoreProperties = Properties()
val keystoreFile = rootProject.file("keystore.properties")
if (keystoreFile.exists()) {
    keystoreProperties.load(FileInputStream(keystoreFile))
    println("DEBUG: storeFile from properties: ${keystoreProperties.getProperty("storeFile")}")
} else {
    println("DEBUG: keystore.properties file NOT found at ${keystoreFile.absolutePath}")
}

android {
    namespace = "com.example.roomatchapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.roomatchapp"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "CLOUD_NAME", "\"${keystoreProperties.getProperty("CLOUD_NAME")?: ""}\"")
        buildConfigField("String", "API_KEY", "\"${keystoreProperties.getProperty("API_KEY")?: ""}\"")
        buildConfigField("String", "API_SECRET", "\"${keystoreProperties.getProperty("API_SECRET")?: ""}\"")
        buildConfigField("String", "GOOGLE_PLACES_API_KEY", "\"${keystoreProperties.getProperty("GOOGLE_PLACES_API_KEY")?: ""}\"")
        buildConfigField("String", "RELEASE_ID_TOKEN", "\"${keystoreProperties.getProperty("RELEASE_ID_TOKEN")?: ""}\"")
    }
    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties.getProperty("storeFile"))
            storePassword = keystoreProperties.getProperty("storePassword")
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"http://roomatch.cs.colman.ac.il:8080\"")
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            buildConfigField("String", "BASE_URL", "\"http://10.0.0.2:8080\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/java", "src/main/kotlin")
        }
        getByName("test") {
            java.srcDirs("src/test/java", "src/test/kotlin")
        }
        getByName("androidTest") {
            java.srcDirs("src/androidTest/java", "src/androidTest/kotlin")
        }
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation (libs.androidx.navigation.compose)
    //UI
    implementation(libs.lottie.compose)
    implementation (libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.material)
    implementation(libs.coil.compose)
    implementation(libs.accompanist.navigation.material)
    implementation(libs.androidx.runtime.saveable)
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)
    implementation(libs.vico.core)
    implementation(libs.charts)
    implementation(libs.accompanist.swiperefresh)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)
    //ktor
    implementation (libs.ktor.client.core)
    implementation (libs.ktor.client.cio)
    implementation (libs.ktor.client.content.negotiation)
    implementation (libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.auth)
    implementation(libs.auth0)
    //retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    //raam-costa for navigation
    implementation(libs.compose.destinations.core)
    ksp(libs.compose.destinations.ksp)
    implementation(libs.bottom.sheet)
    //Google API
    implementation (libs.play.services.auth)
    implementation(libs.places)
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)
    implementation(libs.play.services.location)
    //Data-Base and Cloud
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.cloudinary.android)
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Testing
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    // photos
    implementation (libs.accompanist.pager)
    implementation (libs.accompanist.pager.indicators)

}

ksp {
    arg("compose-destinations.mode", "navgraphs")
    arg("compose-destinations.moduleName", "app")
}

