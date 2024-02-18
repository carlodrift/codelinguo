package fr.unilim.codelinguo.common.model.process

import fr.unilim.codelinguo.common.model.Word
import fr.unilim.codelinguo.common.model.process.parser.JavaFileParser
import fr.unilim.codelinguo.common.model.process.sanitizer.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Files

class FileProcessorTest {

    private fun createTempFileWithLines(prefix: String, extension: String, lines: List<String>): String {
        val tempFile = Files.createTempFile(prefix, extension).toFile()
        tempFile.writeText(lines.joinToString("\n"))
        tempFile.deleteOnExit()
        return tempFile.absolutePath
    }

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

        val filePath = createTempFileWithLines("testJavaFile", ".java", lines)
        val sanitized: List<Word> = JavaFileSanitizer().processFile(filePath)

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
    fun testParseSingleFileJava() {
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

        val filePath = createTempFileWithLines("testAdvancedJavaFile", ".java", lines)
        val sanitized: List<Word> = JavaFileParser().processFile(filePath)

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

        val filePath = createTempFileWithLines("testKotlinFile", ".kt", lines)
        val sanitized: List<Word> = KotlinFileSanitizer().processFile(filePath)

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

        val filePath = createTempFileWithLines("testJSFile", ".js", lines)
        val sanitized: List<Word> = JavascriptFileSanitizer().processFile(filePath)

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

        val filePath = createTempFileWithLines("testHTMLFile", ".html", lines)
        val sanitized: List<Word> = HtmlFileSanitizer().processFile(filePath)

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

        val filePath = createTempFileWithLines("testPythonFile", ".py", lines)
        val sanitized: List<Word> = PythonFileSanitizer().processFile(filePath)

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
