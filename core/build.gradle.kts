plugins {
    kotlin("jvm")
}

group = "com.bzynek"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    val mapperVersion = "2.18.1"
    api("com.fasterxml.jackson.core:jackson-databind:$mapperVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$mapperVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$mapperVersion")

    val ktorVersion = "3.0.0"
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    api("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}