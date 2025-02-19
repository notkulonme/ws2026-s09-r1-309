package hu.notkulonme

import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(CallLogging)
    configureSerialization()
    configureRouting()
}
