plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.gms.google-services") // Firebase
}

android {
    namespace = "com.example.fitunifor"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.fitunifor"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Firebase (usando BOM para gerenciar versões)
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))

    // Dependências do Firebase (sem especificar versão - usará a do BOM)
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-storage-ktx") // ✅ Firebase Storage

    // Glide (para imagens)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.androidx.ui.text.android)
    implementation(libs.generativeai)
    implementation(libs.androidx.gridlayout)
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Testes
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}