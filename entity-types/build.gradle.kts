plugins {
    kotlin("jvm")
}

group = "com.bzynek"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":core"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}