Home Assistant Kotlin WebSocket [Alpha]
===============================

Unofficial library for writing kotlin client for home assistant.

Alternatives:
- [AppDaemon](https://github.com/hassio-addons/addon-appdaemon) - Python Library
- [NetDaemon](https://netdaemon.xyz/) - C# Library
- [Khome](https://github.com/dennisschroeder/khome) - Kotlin Library

Idea of the library is to provide thin extensible **core** layer just handling the WebSocket communication & (de)serialization,
then provide types-specif extensions.

Under the hood:
- [ktor](https://github.com/ktorio/ktor)
- [jackson](https://github.com/FasterXML/jackson-databind)
- [kotlin](https://github.com/JetBrains/kotlin)

Modules:
- **core** - generic access to states & services
- **entity-types**.* - provide type specific extension methods
- **example** - example code how to use the API

Working:
- Watching of state
- Turning of light

TODO:
- Match functionality of [js library](https://github.com/home-assistant/home-assistant-js-websocket)
- Ensure it can correctly recover from connection failure
- Code cleanup
- Deploy as proper library
- ...