package alexa.execute.infrastructure.database

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object GoalsTable : Table("goals") {
    val id = integer("id").autoIncrement()
    val userId = reference("user_id", UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val goalName = varchar("goal_name", 255)
    val startDate = varchar("start_date", 30)
    val endDate = varchar("end_date", 30)
    val streak = integer("streak").default(0)
    val doneDates = text("done_dates") // Store serialized JSON array
    val attachments = text("attachments").nullable() // Optional serialized JSON array

    override val primaryKey = PrimaryKey(id, name = "PK_Goal_ID")
}
