package alexa.execute.infrastructure.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.Dotenv
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    fun init() {
        val dotenv = Dotenv.load()
        val config = HikariConfig().apply {
            jdbcUrl = dotenv["JDBC_URL"]
            driverClassName = dotenv["DRIVER"]
            username = dotenv["POSTGRES_USER"]
            password = dotenv["POSTGRES_PASSWORD"]
            maximumPoolSize = 10
//            maximumPoolSize = dotenv["MAX_POOL_SIZE"].toInt()
        }
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)
    }
}
