package alexa.execute

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import kotlin.collections.joinToString
import kotlin.collections.set

fun Application.configureSecurity() {

    val dotenv = Dotenv.load()
    println(System.getenv().entries.joinToString("\n") { "${it.key}=${it.value}" })


    val jwtAudience = dotenv["AUD"]
        ?: throw IllegalStateException("AUD not set in .env")
    // Specifies the intended recipients of the token.
    // This ensures the token is only valid for applications or services identified by this audience(aud)

    val jwtDomain = dotenv["ISS"]
        ?: throw IllegalStateException("ISS not set in .env")
    // Represents the issuer of the token, typically the domain of the service generating the token.(iss)

    val jwtRealm = dotenv["REALM"]
        ?: throw IllegalStateException("REALM not set in .env")
    // Defines the security realm, which is a way to partition protected resources on the server.

    val jwtSecret = dotenv["JWT_SECRET"]
        ?: throw IllegalStateException("JWT_SECRET not set in .env")
    // Secret to encryption
    // iss matches jwtDomain
    // aud matches jwtAudience

    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }
    routing {
        get("/session/increment") {
            val session = call.sessions.get<MySession>() ?: MySession()
            call.sessions.set(session.copy(count = session.count + 1))
            call.respondText("Counter is ${session.count}. Refresh to increment.")
        }
        authenticate {
            get("/protected") {
                call.respondText("You are authenticated!")
            }
        }
    }
}

@Serializable
data class MySession(val count: Int = 0)

