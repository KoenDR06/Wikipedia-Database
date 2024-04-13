package me.koendev

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

data class Link(val from: Int, val to: Int)

class LinkService(private val database: Database) {
    object Links : Table() {
        val fromID = integer("from_id")
        val toID = integer("to_id")

        override val primaryKey = PrimaryKey(fromID, toID)
    }

    init {
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(Links)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(link: Link) {
        dbQuery {
            Links.insert {
                it[fromID] = link.from
                it[toID] = link.to
            }
        }
    }

    suspend fun readParents(from: Int): List<Int> {
        return dbQuery {
            Links.select { Links.fromID eq from }
                .map { it[Links.toID] }
        }
    }

    suspend fun readChildren(to: Int): List<Int> {
        return dbQuery {
            Links.select { Links.toID eq to }
                .map { it[Links.fromID] }
        }
    }

    suspend fun read(from: Int, to: Int): List<Int> {
        return dbQuery {
            Links.select { (Links.toID eq to) and (Links.fromID eq from) }
                .map { it[Links.fromID] }
        }
    }
}