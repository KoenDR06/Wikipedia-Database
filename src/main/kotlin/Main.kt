package me.koendev

import io.github.cdimascio.dotenv.dotenv
import java.io.File
import java.io.FileWriter

val dotEnv = dotenv()
val txtFile = File("src/main/resources/data.txt")
val writer = FileWriter(txtFile)

fun main() {
    // Going through Wikipedia data
    val f = File(dotEnv["DUMP_FILE"])
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
            val links = processLineFromXML(line)
            for (link in links) {
                writeToFile(title, link)
            }
        }

        if (line.contains("</text>")) {
            inText = false
            title = ""
        }
    }
}

fun processLineFromXML(line: String): List<String> {
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

fun writeToFile(title: String, link: String) {
    writer.write("$title | $link\n")
}