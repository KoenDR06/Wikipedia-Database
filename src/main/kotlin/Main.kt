package me.koendev

import java.io.File

fun main() {
//    val f = File("/home/horseman/Programming/simplewiki-20230820-pages-articles-multistream.xml")
    val f = File("src/main/resources/test-data.xml")
    val reader = f.bufferedReader()

    var inText = false
    var title = ""
    while (true) {
        var line = reader.readLine() ?: break
        if (line.contains("<text ")) {
            inText = true
        }
        if (line.contains("</text>")) {
            inText = false
            title = ""
        }
        if (line.contains("<title>") && !inText) {
            title = line.substring(15, line.length - 8)
        }

        if (inText) {
            val links = processLine(line)
            for (link in links) {
                putInDatabase(title, link)
            }
        }
    }
}

fun processLine(line: String): List<String> {
    val res = mutableListOf<String>()
    var line = line
    if (line.startsWith("[[File:")) {
        val description = line.split("|").last()
        line = description.substring(0, description.length - 2)
    }

    val splits = line.split("[[", "]]")
    for (i in 1..splits.size - 2 step 2) {
        val link = splits[i].split("|")[0].replace(" ", "_")
        if (link.startsWith("wikt:")) {
            continue
        }
        res.add(link)
    }
    return res.toList()
}

fun putInDatabase(title: String, link: String) {
    "$title, $link".println()
}