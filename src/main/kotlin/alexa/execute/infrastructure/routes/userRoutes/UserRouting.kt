package alexa.execute.infrastructure.routes.userRoutes

import alexa.execute.domain.repository.UserRepository
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import kotlinx.serialization.json.Json
import org.h2.util.json.JSONArray

fun Application.userRouting(userRepository: UserRepository) {
    routing{
        route("/user"){

            get("/all"){
                call.respondText(
                    userRepository.getAllUsers().toString()
                )
            }

            get("/{id}"){

            }

            post("/create"){

            }

        }
    }
}