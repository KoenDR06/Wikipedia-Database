package me.koendev.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.dotenv
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

val dotEnv = dotenv()
lateinit var articleService: ArticleService
lateinit var linkService: LinkService

var count = 0
var numberOfLinks: Int = 0
val startTimeMillis = System.currentTimeMillis()

fun main() {
    var reader = BufferedReader(FileReader("src/main/resources/data.txt"))
    while (reader.readLine() != null) numberOfLinks++
    reader.close()

    // Setting up DataBase
    val config = HikariConfig()
    config.jdbcUrl = dotEnv["DB_URL"]
    config.username = dotEnv["DB_USER"]
    config.password = dotEnv["DB_PASSWORD"]
    config.driverClassName = "org.mariadb.jdbc.Driver"
    config.maximumPoolSize = 10
    config.connectionTimeout = 30000
    config.idleTimeout = 600000
    val datasource = HikariDataSource(config)


    articleService = ArticleService(datasource)
    linkService = LinkService(datasource)

    // Going through Wikipedia data
    val f = File("src/main/resources/data.txt")
    reader = f.bufferedReader()

    while (true) {
        val line = reader.readLine() ?: break
        val title = line.split(" | ")[0]
        val link = line.split(" | ")[1]

        putInDatabase(title, link)
        predictETA()
    }
}

fun putInDatabase(title: String, link: String) {
    var articleID = articleService.read(title)
    if (articleID == null) {
        articleID = articleService.create(Article(title))
    }

    var linkID = articleService.read(link)
    if (linkID == null) {
        linkID = articleService.create(Article(link))
    }

    if (linkService.read(articleID, linkID) == null) {
        linkService.create(Link(articleID, linkID))
    }
}

fun predictETA() {
    count++
    if(count % 1_000 == 0) {
        println("ETA: ${(numberOfLinks / count) * (System.currentTimeMillis() - startTimeMillis)}\tprocessed: $count / $numberOfLinks\truntime: ${System.currentTimeMillis() - startTimeMillis} millis")
    }
}