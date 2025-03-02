package alexa.execute.infrastructure.routes.goalRoutes

import alexa.execute.domain.model.goal.Goal
import alexa.execute.infrastructure.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.goalRouting(userService: UserService) {
    routing {
        authenticate("jwt-auth") {
            get("/goal/all") {
                val goals = userService.getAllGoals()
                call.respond(goals)
            }
            get("/goal/user/{user_id}") {
                val userId = call.parameters["user_id"]?.toIntOrNull()

                when {
                    (userId != null) -> {
                        val goals = userService.getUserGoals(userId)
                        call.respond(goals)
                        return@get
                    }

                    else -> {
                        call.respond("Something went wrong")
                        return@get
                    }
                }

            }
            post("/goal/create") {
                var currentUser: Int = 0
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.payload?.getClaim("email")?.asString()
                if (email != null) {
                    currentUser = userService.getUserByEmail(email)?.id ?: 0
                }

                val goalToCreate = call.receive<Goal>()
                goalToCreate.userId = currentUser
                val newGoal = userService.createGoal(goalToCreate)
                call.respond(HttpStatusCode.Created, "goal number $newGoal created!")
                return@post

            }
            delete("/goal/{id}/delete") {
                val goalToDelete = call.parameters["id"]?.toIntOrNull()

                when {
                    (goalToDelete != null) -> {
                        userService.deleteGoal(goalToDelete)
                        call.respond("Goal has been deleted!")
                        return@delete
                    }

                    else -> {
                        call.respond("Something went wrong")
                        return@delete
                    }
                }
            }
            post("/goal/{id}/add_date") {
                val goalId = call.parameters["id"]?.toIntOrNull()
                val newDate = call.receive<String>()

                if (goalId == null || goalId <= 0) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid goal ID")
                    return@post
                }

                userService.addDateToGoal(goalId, newDate)
                call.respond(HttpStatusCode.OK, "Date added successfully")
            }
        }
    }
}