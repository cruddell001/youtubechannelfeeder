import java.util.*

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.9.23"
    id("io.ktor.plugin") version "2.3.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"
}

group = "com.ruddell"
version = "0.0.1"

application {
    mainClass.set("com.ruddell.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

    // Youtube API
    implementation("com.google.http-client:google-http-client-gson:1.19.0")
    implementation("com.google.api-client:google-api-client:1.23.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.23.0")
    implementation("com.google.apis:google-api-services-youtube:v3-rev222-1.25.0")

    // networking
    val ktor_version = "2.1.1"
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-apache:$ktor_version")
    implementation("io.ktor:ktor-client-java:$ktor_version")
    implementation("io.ktor:ktor-client-gson:$ktor_version")
    implementation("com.eatthepath:pushy:0.14.2")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    // database
    implementation("mysql:mysql-connector-java:8.0.30")
}

fun loadFromLocalPropertiesFile(): Properties {
    val properties = Properties()
    val propertiesFile = File("local.properties")
    if (propertiesFile.exists()) {
        properties.load(propertiesFile.inputStream())
    }
    return properties
}

fun generateBuildConfigFile() {
    println("generateBuildConfigFile: ${project.rootDir.absolutePath}")
    val properties = loadFromLocalPropertiesFile()
    val generatedDir = File(project.rootDir, "src/generated/kotlin/com/ruddell")
    val pathCreated = generatedDir.exists() || generatedDir.mkdirs()
    println("pathCreated: $pathCreated")
    if (!pathCreated) return
    val buildConfigFile = File(generatedDir, "BuildConfig.kt")
    val youtubeKey = properties.getProperty("youtubeApiKey")
    println("Generating BuildConfig file with youtubeApiKey: $youtubeKey")
    buildConfigFile.writeText(
        """
            package com.ruddell
            
            object BuildConfig {
                const val YOUTUBE_API_KEY: String = "$youtubeKey"
            }
            """.trimIndent()
    )
}

// task to generate the BuildConfig file
tasks.register("generateBuildConfigFile") {
    group = "build"
    description = "Generate BuildConfig.kt file"
    doLast {
        println("running generateBuildConfigFile")
        generateBuildConfigFile()
    }
}

// call generateBuildConfigFile when the build gradle task is run
tasks.getByName("build").dependsOn("generateBuildConfigFile")

sourceSets {
    val main by getting {
        kotlin.srcDir("src/generated/kotlin")
    }
}