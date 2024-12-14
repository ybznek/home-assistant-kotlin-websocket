plugins {
    kotlin("jvm") version "2.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    compilerOptions {
        if (System.getProperty("idea.active") == "true") {
            freeCompilerArgs = listOf("-Xdebug")
        }
    }

    jvmToolchain(21)
}