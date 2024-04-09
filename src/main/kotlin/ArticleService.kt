package me.koendev

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

data class Article(val title: String)

class ArticleService(private val database: Database) {
    object Users : Table() {
        val id = integer("id").autoIncrement()
        val title = varchar("title", length = 256)

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(article: Article): Int = dbQuery {
        Users.insert {
            it[title] = article.title
        }[Users.id]
    }

    suspend fun read(id: Int): Article? {
        return dbQuery {
            Users.select { Users.id eq id }
                .map { Article(it[Users.title]) }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, article: Article) {
        dbQuery {
            Users.update({ Users.id eq id }) {
                it[title] = article.title
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Users.deleteWhere { Users.id.eq(id) }
        }
    }
}