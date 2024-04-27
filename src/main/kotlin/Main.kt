package me.koendev

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.dotenv
import me.koendev.database.Article
import me.koendev.database.ArticleService
import me.koendev.database.Link
import me.koendev.database.LinkService
import java.io.File

val dotEnv = dotenv()
lateinit var articleService: ArticleService
lateinit var linkService: LinkService

var lastTimeAdded = System.currentTimeMillis()
var count = 0
const val numberOfLinks = 8027607
val startTimeMillis = System.currentTimeMillis()

fun main() {
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
    val f = File("/home/horseman/Programming/simplewiki.xml")
//    val f = File("src/main/resources/test-data.xml")
    val reader = f.bufferedReader()

    var inText = false
    var title = ""
    while (true) {
        val line = reader.readLine() ?: break
        if (line.contains("<text ")) {
            inText = true
        }

        if (line.contains("<title>") && !inText) {
            title = line.strip().substring(7, line.strip().length - 8)
        }

        if (inText) {
            val links = processLine(line)
            for (link in links) {
                putInDatabase(title, link)
                predictETA()
            }
        }

        if (line.contains("</text>")) {
            inText = false
            title = ""
        }
    }
}

fun processLine(line: String): List<String> {
    val res = mutableListOf<String>()
    var line = line
    if (line.startsWith("[[File:")) {
        val description = line.split("|").last()
        if(description.length >= 2) {
            line = description.substring(0, description.length - 2)
        }
    }

    val splits = line.split("[[")
    for (i in 1..< splits.size) {
        val link = splits[i].split("]]")[0].split("|")[0].replace(" ", "_")
        if (link.startsWith("wikt:") || link.startsWith("File:")) {
            continue
        }
        res.add(link)
    }
    return res.toList()
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
    val currentTime = System.currentTimeMillis()
    val linksToGo = numberOfLinks - ++count
    val deltaTime = currentTime - lastTimeAdded

    lastTimeAdded = currentTime

    if(count % 1_000 == 0) {
        println("Estimated time remaining: ${linksToGo * deltaTime} millis\tprocessed: $count / $numberOfLinks\truntime: ${currentTime - startTimeMillis} millis")
    }
}