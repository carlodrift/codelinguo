package fr.unilim.saes5

import fr.unilim.saes5.model.reader.JavaFileReader
import fr.unilim.saes5.service.WordAnalyticsService

fun main(args: Array<String>) {
    val exampleFilePath = "example/Gameplay.java"
    val words = JavaFileReader().readOne(exampleFilePath)
    println(words.size.toString() + " mots ont été trouvés :")

    println("---------------------------------------------")

    val analytics = WordAnalyticsService()
    val wordRank = analytics.wordRank(words)

    println("Voici la liste des mots trouvés par occurence")
    for ((word, count) in wordRank) {
        println("$word  $count")
    }


}
