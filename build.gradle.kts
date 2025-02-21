plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "1.8.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "hu.notkulonme.import_script"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktor_version: String by project
val logback_version: String by project

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-okhttp:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar{
    manifest{
        attributes["Main-Class"] = "hu.notkulonme.import_script.MainKt"
    }
    archiveBaseName.set("import script")
    archiveVersion.set("1.0")
}

tasks.shadowJar{
    manifest{
        attributes["Main-Class"] = "hu.notkulonme.import_script.MainKt"
    }
    archiveBaseName.set("import-script")
    archiveVersion.set("")
    archiveClassifier.set("")
}

kotlin {
    jvmToolchain(21)
}