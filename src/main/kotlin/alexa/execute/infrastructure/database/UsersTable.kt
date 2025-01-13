package alexa.execute.infrastructure.database

import org.jetbrains.exposed.sql.Table

object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 255).uniqueIndex()
    val nickname = varchar("nickname", 50)
    val password = varchar("password", 255)
    val age = integer("age")

    override val primaryKey = PrimaryKey(id, name = "PK_User_ID")
}


