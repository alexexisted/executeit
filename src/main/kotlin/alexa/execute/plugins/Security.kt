package alexa.execute.plugins

import alexa.execute.infrastructure.services.UserService
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity(userService: UserService) {

    val dotenv = Dotenv.load()

    val jwtRealm = dotenv["REALM"]
        ?: throw IllegalStateException("REALM not set in .env")
    // Defines the security realm, which is a way to partition protected resources on the server.

    authentication {
        jwt("jwt-auth") {
            realm = jwtRealm
            verifier(userService.jwtVerifier)
            validate { credential ->
                credential.payload.getClaim("email").asString()?.let { JWTPrincipal(credential.payload) }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Invalid or expired token")
            }
            authHeader { call ->

                val authHeader = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                if (!authHeader.isNullOrBlank()) {
                    return@authHeader HttpAuthHeader.Single("Bearer", authHeader)
                }

                val cookieToken = call.request.cookies["token"]
                if (!cookieToken.isNullOrBlank()) {
                    return@authHeader HttpAuthHeader.Single("Bearer", cookieToken)
                }
                null
            }
        }
    }
}

