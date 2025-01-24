package alexa.execute.infrastructure.database

import alexa.execute.infrastructure.services.UserService
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val database = Database.connect(hikari())
        transaction {
            SchemaUtils.create(UsersTable) //create table
            SchemaUtils.create(GoalsTable) // create another table
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig("/hikari.properties")
        return HikariDataSource(config)
    }
}
