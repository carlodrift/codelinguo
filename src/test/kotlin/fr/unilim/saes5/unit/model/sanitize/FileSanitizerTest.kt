package fr.unilim.saes5.unit.model.sanitize

import fr.unilim.saes5.model.Word
import fr.unilim.saes5.model.sanitize.JavaFileSanitizer
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class FileSanitizerTest {
    @Test
    fun testSanitizeSingleFile(): Unit {
        val lines = listOf(
            "boolean success = board.placeToken(column, activePlayer);",
            "if (board.checkVictory(column)) {"
        )

        val sanitized: List<Word> = JavaFileSanitizer().sanitizeLines(lines);

        Assertions.assertThat(sanitized.toSet()).isEqualTo(
            setOf(
                Word("success"),
                Word("board"),
                Word("place"),
                Word("token"),
                Word("column"),
                Word("active"),
                Word("player"),
                Word("check"),
                Word("victory"),
            )

        );
    }
}