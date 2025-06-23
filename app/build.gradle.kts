plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.prm.ocs"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.prm.ocs"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }
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
    packaging {
        resources {
            excludes.add("/META-INF/NOTICE.md")
            excludes.add("/META-INF/LICENSE.md")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.gson)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Thêm Room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    implementation (libs.play.services.auth)
//    implementation (libs.credentials)
//    implementation (libs.credentials.play.services.auth)

    // Thêm JavaMail
    implementation(libs.javamail.mail)
    implementation(libs.javamail.activation)

    implementation (libs.okhttp)
    implementation ("androidx.browser:browser:1.5.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    implementation ("com.google.android.material:material:1.10.0")
    implementation("androidx.cardview:cardview:1.0.0")

    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")



    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}