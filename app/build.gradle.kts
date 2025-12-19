plugins {
    id("com.android.application")
}

android {
    namespace = "org.example"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.schoolescape.game"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
        create("release") {
            // Keystore file path - relative to root project directory
            val keystorePropertiesFile = rootProject.file("keystore.properties")
            if (keystorePropertiesFile.exists()) {
                val props = mutableMapOf<String, String>()
                keystorePropertiesFile.readLines().forEach { line ->
                    val trimmed = line.trim()
                    if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
                        val split = trimmed.split("=", limit = 2)
                        if (split.size == 2) {
                            props[split[0].trim()] = split[1].trim()
                        }
                    }
                }
                
                val storeFileProp = props["storeFile"]
                val storePasswordProp = props["storePassword"]
                val keyAliasProp = props["keyAlias"]
                val keyPasswordProp = props["keyPassword"]
                
                if (!storeFileProp.isNullOrBlank() && !storePasswordProp.isNullOrBlank() && 
                    !keyAliasProp.isNullOrBlank() && !keyPasswordProp.isNullOrBlank()) {
                    // Resolve the keystore file path relative to root project
                    val keystoreFile = if (storeFileProp.startsWith("../")) {
                        // Remove "../" and resolve from root project
                        rootProject.file(storeFileProp.substring(3))
                    } else if (storeFileProp.startsWith("./")) {
                        rootProject.file(storeFileProp.substring(2))
                    } else {
                        // Assume path is relative to root project
                        rootProject.file(storeFileProp)
                    }
                    
                    if (keystoreFile.exists()) {
                        storeFile = keystoreFile
                        storePassword = storePasswordProp
                        keyAlias = keyAliasProp
                        keyPassword = keyPasswordProp
                    } else {
                        println("WARNING: Keystore file not found at: ${keystoreFile.absolutePath}")
                    }
                } else {
                    println("WARNING: Keystore properties are incomplete in keystore.properties")
                }
            } else {
                println("WARNING: keystore.properties file not found. Release builds will not be signed.")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Only set signing config if keystore is properly configured
            val releaseSigningConfig = signingConfigs.findByName("release")
            if (releaseSigningConfig != null && releaseSigningConfig.storeFile != null) {
                signingConfig = releaseSigningConfig
            }
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}
