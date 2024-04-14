package me.koendev

import java.io.File
import org.jetbrains.exposed.sql.Database
import io.github.cdimascio.dotenv.dotenv
import me.koendev.database.*

val dotEnv = dotenv()
lateinit var articleService: ArticleService
lateinit var linkService: LinkService

fun main() {
    // Setting up DataBase
    val database = Database.connect(
        url = dotEnv["DB_URL"],
        user = dotEnv["DB_USER"],
        password = dotEnv["DB_PASSWORD"],
        driver = "org.mariadb.jdbc.Driver"
    )
    articleService = ArticleService(database)
    linkService = LinkService(database)

    // Going through Wikipedia data
    val f = File("/home/horseman/Programming/simplewiki-20230820-pages-articles-multistream.xml")
//    val f = File("src/main/resources/test-data.xml")
    val reader = f.bufferedReader()

    var count = 0

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
            }
        }

        if (line.contains("</text>")) {
            inText = false
            title = ""
            count++
            count.println()
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
        if (link.startsWith("wikt:")) {
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

    var linkedID = articleService.read(link)
    if (linkedID == null) {
        linkedID = articleService.create(Article(link))
    }

    if (linkService.read(articleID, linkedID) == null) {
        linkService.create(Link(articleID, linkedID))
    }
}