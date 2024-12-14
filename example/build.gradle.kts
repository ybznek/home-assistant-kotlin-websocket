plugins {
    kotlin("jvm")
    id("com.google.cloud.tools.jib") version ("3.4.4")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":entity-types:light"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

jib {
    from {
        image = "gcr.io/distroless/java21-debian12"
        platforms {
            platform {
                architecture = "arm64"
                os = "linux"
            }
        }
    }
    to {
        // Replace with your desired image name
        image = "my-app:1.0"
        // Uncomment and modify for Docker Hub or other registries
        // image = "docker.io/username/my-app:1.0"
    }
    container {
        mainClass = "MainKt"  // Replace with your main class
        //ports = listOf("8080")
        // For Spring Boot applications, use this instead:
        // jvmFlags = listOf("-Xms512m", "-Xmx512m")
    }
}
