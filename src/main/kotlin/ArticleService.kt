package me.koendev

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

data class Article(val title: String)

class ArticleService(private val database: Database) {
    object Articles : Table() {
        val id = integer("id").autoIncrement()
        val title = varchar("title", length = 256)

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(Articles)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(article: Article): Int {
        val id = dbQuery {
            Articles.insert {
                it[title] = article.title
            }[Articles.id]
        }
        return id
    }

    suspend fun read(title: String): Int? {
        return dbQuery {
            Articles.select { Articles.title eq title }
                .map { it[Articles.id] }
                .singleOrNull()
        }
    }
}