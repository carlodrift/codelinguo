package fr.unilim.codelinguo.unit.model.sanitize

import fr.unilim.codelinguo.model.Word
import fr.unilim.codelinguo.model.sanitize.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Files

class FileSanitizerTest {
    @Test
    fun testSanitizeSingleFileJava() {
        val lines = listOf(
            "package com.example.project;",
            "import java.util.List;",
            "// This is a single-line comment",
            "/* This is a block comment",
            "   spanning multiple lines */",
            "/* Start of block comment",
            "   Still inside block comment",
            "   End of block comment */",
            "boolean success = board.placeToken(column, activePlayer);",
            "if (board.checkVictory(column)) {"
        )

        val sanitized: List<Word> = JavaFileSanitizer().sanitizeLines(lines, "")

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

        )
    }

    @Test
    fun testSanitizeAdvancedSingleFileJava() {
        val lines = listOf(
            "package com.example.project;",
            "",
            "import java.util.List;",
            "",
            "public class ExampleClass {",
            "    // This is a single-line comment",
            "    /* This is a block comment",
            "       spanning multiple lines */",
            "    /* Start of block comment",
            "       Still inside block comment",
            "       End of block comment */",
            "    public void exampleMethod() {",
            "        boolean success = board.placeToken(column, activePlayer);",
            "        if (board.checkVictory(column)) {",
            "            // Method logic here",
            "        }",
            "    }",
            "}"
        )

        val tempFile = Files.createTempFile("testFile", ".java").toFile()

        tempFile.deleteOnExit()

        tempFile.writeText(lines.joinToString("\n"))

        val sanitizer = AdvancedJavaFileSanitizer()
        val sanitized: List<Word> = sanitizer.sanitizeLines(lines, tempFile.absolutePath)

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
                Word("example"),
                Word("class"),
                Word("method"),
            )
        )

        tempFile.delete()
    }

    @Test
    fun testSanitizeSingleFileKotlin() {
        val lines = listOf(
            "package com.example.project",
            "import kotlin.collections.List",
            "// This is a single-line comment",
            "/* This is a block comment",
            "   spanning multiple lines */",
            "val success = board.placeToken(column, activePlayer)",
            "if (board.checkVictory(column)) {"
        )

        val sanitized: List<Word> = KotlinFileSanitizer().sanitizeLines(lines, "")

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

        )
    }

    @Test
    fun testSanitizeSingleFileJavascript() {
        val lines = listOf(
            "import { Board } from 'board-module';",
            "// This is a single-line comment",
            "/* This is a block comment",
            "   spanning multiple lines */",
            "let success = board.placeToken(column, activePlayer);",
            "if (board.checkVictory(column)) {"
        )

        val sanitized: List<Word> = JavascriptFileSanitizer().sanitizeLines(lines, "")

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

        )
    }

    @Test
    fun testSanitizeSingleFileHtml() {
        val lines = listOf(
            "<html>",
            "<head>",
            "<title>Sample Page</title>",
            "</head>",
            "<body>",
            "<script>",
            "// This is a single-line comment",
            "/* This is a block comment",
            "   spanning multiple lines */",
            "let success = board.placeToken(column, activePlayer);",
            "if (board.checkVictory(column)) {",
            "   // JavaScript code here",
            "}",
            "</script>",
            "</body>",
            "</html>"
        )

        val sanitized: List<Word> = HtmlFileSanitizer().sanitizeLines(lines, "")

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

        )
    }

    @Test
    fun testSanitizeSingleFilePython() {
        val lines = listOf(
            "import board",
            "# This is a single-line comment",
            "\"\"\" This is a block comment",
            "    This line should be ignored as it's inside the block comment",
            "    spanning multiple lines \"\"\"",
            "success = board.place_token(column, active_player)",
            "if board.check_victory(column):",
            "\"\"\"Single line block comment\"\"\"",
            "'''Another form of block comment'''"
        )

        val sanitized: List<Word> = PythonFileSanitizer().sanitizeLines(lines, "")

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

        )
    }
}