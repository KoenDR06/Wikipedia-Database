package me.koendev

import java.io.File
import kotlin.concurrent.thread

val outFile = File("data.sql").bufferedWriter()
val inFile = File("simplewiki.xml").bufferedReader()
val reader = inFile.lineSequence().iterator()
var count: ULong = 0u
var isFirst = true

fun main() {
    outFile.append("INSERT INTO Links VALUES \n")

    val start = System.currentTimeMillis()
    thread(isDaemon = true) {
        while (true) {
            print(String.format("%.4f%%", (count.toDouble() / 23474888 * 100))+"  ${(System.currentTimeMillis()-start)/1000} seconds\r")
            Thread.sleep(1000)
        }
    }

    while (reader.hasNext()) {
        val line = reader.next()

        if (line.contains("<page>")) {
            processPage()
        }
    }
    outFile.close()
    inFile.close()
}

fun processPage() {
    val pageTitle = reader.next()
        .replace(Regex("</?title>"), "")
        .trimIndent()
        .replace(",", "|COMMA;|")
        .replace("'", "''")
    count++

    // Wait until we enter text
    var line = reader.next()
    count++
    while (!line.matches(Regex("^ *?<text.*?>.*?$"))) {
        line = reader.next()
        count++
    }
    line = line.replace(Regex(".*?<text.*?>"), "")

    // Process line
    do {
        if (line.startsWith("[[File:")) {
            val description = line.split("|").last()
            if(description.length >= 2) {
                line = description.substring(0, description.length - 2)
            }
        }

        val splits = line.split("[[")
        for (i in 1..< splits.size) {
            val link = splits[i].split("]]")[0].split("|")[0]
            if (link.startsWith("wikt:") || link.startsWith("File:")) {
                continue
            }
            val prefix = if (isFirst) {
                isFirst = false
                ""
            } else {
                ",\n"
            }

            outFile.append(prefix + "('$pageTitle', '${link.replace(",", "|COMMA;|").replace("'", "''")}')")
        }
        line = reader.next()
        count++
    }
    while ((!line.matches(Regex("^.*?</text.*?>$"))))

    return
}