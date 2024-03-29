import java.util.Properties
plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.temperature"
    compileSdk = 34

    buildFeatures {
        android.buildFeatures.buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.temperature"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val keystoreFile = project.rootProject.file("apikeys.properties")
        val properties = Properties()
        properties.load(keystoreFile.inputStream())

        val apiKey= properties.getProperty("API_KEY") ?: ""
            buildConfigField(
                type = "String",
                name = "API_KEY",
                value = apiKey
            )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("com.google.android.gms:play-services-location:18.0.0")
    implementation("androidx.annotation:annotation:1.3.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}