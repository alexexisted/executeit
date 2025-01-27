package alexa.execute

import alexa.execute.infrastructure.database.DatabaseFactory
import alexa.execute.plugins.configureHTTP
import alexa.execute.plugins.configureSecurity
import alexa.execute.plugins.configureSerialization
import alexa.execute.infrastructure.routes.configureRouting
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain.main(args)
}

fun Application.module() {
    configureSecurity()
    configureSerialization()
    configureHTTP()
    configureRouting()
    DatabaseFactory.init()
}
