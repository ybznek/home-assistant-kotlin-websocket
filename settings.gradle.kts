plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "home-assistant-kotlin-websocket"
include("core")
include("entity-types")
include("entity-types:light")
include("example")
