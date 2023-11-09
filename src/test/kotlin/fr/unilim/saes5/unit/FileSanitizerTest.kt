package fr.unilim.saes5.unit

import fr.unilim.saes5.model.sanitize.FileSanitizer
import fr.unilim.saes5.model.Word
import fr.unilim.saes5.model.sanitize.JavaFileSanitizer
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class FileSanitizerTest {
    @Test
    fun testSanitizeSingleFile(): Unit {
        val lines = listOf("boolean success = board.placeToken(column, activePlayer);",
                            "if (board.checkVictory(column)) {")

        val sanitized : List<Word> = JavaFileSanitizer().sanitizeLines(lines);

        Assertions.assertThat(sanitized).isEqualTo(
            listOf(
                    Word("success"),
                    Word("board"),
                    Word("placeToken"),
                    Word("column"),
                    Word("activePlayer"),
                    Word("board"),
                    Word("checkVictory"),
                    Word("column"),
            )

        );
    }
}