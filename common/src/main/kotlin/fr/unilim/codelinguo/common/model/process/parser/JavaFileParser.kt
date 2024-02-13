package fr.unilim.codelinguo.common.model.process.parser

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.body.*
import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.stmt.CatchClause
import com.github.javaparser.ast.stmt.ForEachStmt
import com.github.javaparser.ast.stmt.LocalClassDeclarationStmt
import com.github.javaparser.ast.stmt.TryStmt
import com.github.javaparser.ast.type.TypeParameter
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import fr.unilim.codelinguo.common.model.Word
import fr.unilim.codelinguo.common.model.process.FileProcessor
import fr.unilim.codelinguo.common.model.process.sanitizer.JavaFileSanitizer
import java.io.File
import java.util.*


class JavaFileParser : FileProcessor() {
    override val reservedKeywords = loadReservedKeywords("advanced_java")

    override fun processFile(path: String): List<Word> {
        val file = File(path)
        val allWords = mutableListOf<String>()
        file.inputStream().use { inputStream ->
            val parseResult = JavaParser().parse(inputStream)
            if (parseResult.result.isPresent) {
                val cu = parseResult.result.get()

                cu.accept(object : VoidVisitorAdapter<Void?>() {
                    override fun visit(n: ClassOrInterfaceDeclaration, arg: Void?) {
                        super.visit(n, arg)
                        allWords.addAll(splitIdentifierIntoWords(n.nameAsString))
                    }

                    override fun visit(n: MethodDeclaration, arg: Void?) {
                        super.visit(n, arg)
                        allWords.addAll(splitIdentifierIntoWords(n.nameAsString))
                    }

                    override fun visit(n: VariableDeclarator, arg: Void?) {
                        super.visit(n, arg)
                        allWords.addAll(splitIdentifierIntoWords(n.nameAsString))
                    }

                    override fun visit(n: Parameter, arg: Void?) {
                        super.visit(n, arg)
                        allWords.addAll(splitIdentifierIntoWords(n.nameAsString))
                    }

                    override fun visit(n: NameExpr, arg: Void?) {
                        super.visit(n, arg)
                        allWords.addAll(splitIdentifierIntoWords(n.nameAsString))
                    }

                    override fun visit(n: MethodCallExpr, arg: Void?) {
                        super.visit(n, arg)
                        allWords.addAll(splitIdentifierIntoWords(n.nameAsString))
                    }

                    override fun visit(n: ConstructorDeclaration, arg: Void?) {
                        super.visit(n, arg)
                        allWords.addAll(splitIdentifierIntoWords(n.nameAsString))
                    }

                    override fun visit(n: LocalClassDeclarationStmt, arg: Void?) {
                        super.visit(n, arg)
                        n.classDeclaration.nameAsString?.let {
                            allWords.addAll(splitIdentifierIntoWords(it))
                        }
                    }

                    override fun visit(n: EnumDeclaration, arg: Void?) {
                        super.visit(n, arg)
                        allWords.addAll(splitIdentifierIntoWords(n.nameAsString))
                    }

                    override fun visit(n: AnnotationDeclaration, arg: Void?) {
                        super.visit(n, arg)
                        allWords.addAll(splitIdentifierIntoWords(n.nameAsString))
                    }

                    override fun visit(n: ForEachStmt, arg: Void?) {
                        super.visit(n, arg)
                        n.variable.variables.forEach { variableDeclarator ->
                            allWords.addAll(splitIdentifierIntoWords(variableDeclarator.nameAsString))
                        }
                    }

                    override fun visit(n: CatchClause, arg: Void?) {
                        super.visit(n, arg)
                        allWords.addAll(splitIdentifierIntoWords(n.parameter.nameAsString))
                    }

                    override fun visit(n: LambdaExpr, arg: Void?) {
                        super.visit(n, arg)
                        n.parameters.forEach { param ->
                            allWords.addAll(splitIdentifierIntoWords(param.nameAsString))
                        }
                    }

                    override fun visit(n: MethodReferenceExpr, arg: Void?) {
                        super.visit(n, arg)
                        allWords.addAll(splitIdentifierIntoWords(n.identifier))
                    }

                    override fun visit(n: TryStmt, arg: Void?) {
                        super.visit(n, arg)
                        n.resources.forEach { resource ->
                            if (resource.isVariableDeclarationExpr) {
                                val expr = resource as VariableDeclarationExpr
                                expr.variables.forEach { variable ->
                                    allWords.addAll(splitIdentifierIntoWords(variable.nameAsString))
                                }
                            }
                        }
                    }

                    override fun visit(n: TypeParameter, arg: Void?) {
                        super.visit(n, arg)
                        allWords.addAll(splitIdentifierIntoWords(n.nameAsString))
                    }

                    override fun visit(n: VariableDeclarationExpr, arg: Void?) {
                        super.visit(n, arg)
                        n.variables.forEach { variable ->
                            allWords.addAll(splitIdentifierIntoWords(variable.nameAsString))
                        }
                    }
                }, null)
            }
        }

        var result = allWords.map { Word(it) }
        if (result.isEmpty()) {
            result = JavaFileSanitizer().processFile(path)
            println("JavaFileParser: falling back to JavaFileSanitizer for $path")
        }
        return result
    }

    private fun splitIdentifierIntoWords(identifier: String): List<String> {
        if (reservedKeywords.contains(identifier.lowercase(Locale.getDefault()))) {
            return emptyList()
        }

        if (identifier.uppercase(Locale.getDefault()) == identifier) {
            return identifier.split('_')
                .filter { it.isNotEmpty() }
                .map { it.lowercase(Locale.getDefault()) }
                .filter { it.length > 2 && it.all { char -> char.isLetter() } && !reservedKeywords.contains(it) }
        }

        val cleanedIdentifier = identifier.filter { it.isLetter() }

        val words = cleanedIdentifier.split("(?<!^)(?=[A-Z])".toRegex())
            .map { it.lowercase(Locale.getDefault()) }

        return words.filter { it.length > 2 && it.all { char -> char.isLetter() } && !reservedKeywords.contains(it) }
    }
}
