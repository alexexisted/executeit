package alexa.execute

import alexa.execute.infrastructure.database.DatabaseFactory
import alexa.execute.infrastructure.routes.configureRouting
import alexa.execute.infrastructure.services.UserService
import alexa.execute.plugins.configureHTTP
import alexa.execute.plugins.configureSecurity
import alexa.execute.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain.main(args)
}

fun Application.module() {
    val userService = UserService()
    configureSecurity(userService)
    configureSerialization()
    configureHTTP()
    configureRouting(userService)
    DatabaseFactory.init()
}
