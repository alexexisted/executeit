package alexa.execute

import io.github.cdimascio.dotenv.Dotenv
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain.main(args)
}

fun Application.module() {
    configureSecurity()
    configureHTTP()
    configureRouting()
//    configureDatabases()
//    configureSerialization()
}
