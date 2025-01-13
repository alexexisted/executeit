package alexa.execute.infrastructure.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        Database.connect(hikari())
        transaction {
            SchemaUtils.create(UsersTable) //create table
            UsersTable.insert { //insert fake info into table
                it[id] = 1
                it[email] = "alexa@admin.com"
                it[nickname] = "alexa_diamant"
                it[password] = "alexas777secret"
                it[age] = 20
            }
            SchemaUtils.create(GoalsTable) // create another table
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig("/hikari.properties")
        return HikariDataSource(config)
    }
}
