package fr.unilim.saes5

import fr.unilim.saes5.model.reader.SingleFileReader

fun main(args: Array<String>) {
    val words = SingleFileReader().readOne("example/Gameplay.java")

    for (word in words) {
        println(word)
    }
}
