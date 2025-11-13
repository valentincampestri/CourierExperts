plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.courierexperts.demo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.courierexperts.demo"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", '"https://api.example.com/"')
        }
        debug {
            isMinifyEnabled = false
            buildConfigField("String", "BASE_URL", '"http://10.0.2.2:8080/"')
        }
    }

    // 🔧 Java 17 (para que no choque la toolchain)
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // 🧩 ViewBinding para usar bindings en Activities/Adapters
    buildFeatures {
        viewBinding = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.6")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.6")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.cardview:cardview:1.0.0")

    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Firebase Auth + Google Sign-In
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    // Firestore for profile sync
    implementation("com.google.firebase:firebase-firestore")
    // WorkManager for offline retries
    implementation("androidx.work:work-runtime:2.9.0")
    // ListenableFuture API required by WorkManager
    implementation("androidx.concurrent:concurrent-futures:1.1.0")
    // Provide actual Guava ListenableFuture (android variant)
    implementation("com.google.guava:guava:32.1.3-android")

    // Credential Manager + Google Identity Services (nuevo flujo de Google Sign-In)
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")
}

// Task helpers to compile with -Xlint flags and surface deprecation details
tasks.register("compileDebugWithLint") {
    doFirst {
        tasks.withType<org.gradle.api.tasks.compile.JavaCompile>().configureEach {
            options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
        }
    }
    dependsOn("compileDebugJavaWithJavac")
}

tasks.register("compileReleaseWithLint") {
    doFirst {
        tasks.withType<org.gradle.api.tasks.compile.JavaCompile>().configureEach {
            options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
        }
    }
    dependsOn("compileReleaseJavaWithJavac")
}

// Always enable -Xlint:deprecation details for Java compile tasks (visible in Android Studio builds)
tasks.withType<org.gradle.api.tasks.compile.JavaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
}
