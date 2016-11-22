package net.kimleo.rec.init

import net.kimleo.rec.exit
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*

class Initializer(val file: String, val parameters: HashMap<String, String> = linkedMapOf()) {

    val requiredProperties = listOf("name", "delimiter", "escape", "format")

    fun init() {
        val recFile = file + ".rec"

        val reader = BufferedReader(InputStreamReader(System.`in`));
        for (property in requiredProperties) {
            if (property !in parameters) {
                println("Please input the $property: ")
                val value = reader.readLine()
                if (!checkProperty(property, value)) { exit("Unexpected $property: $value") }
                parameters[property] = value
            }
        }

        val outputFile = File(recFile)
        val writer = outputFile.writer()
        writer.appendln(parameters["name"])

        for (property in parameters.keys) {
            if (property != "name") {
                writer.appendln("$property=${parameters[property]}")
            }
        }

        writer.close()
    }

    private fun checkProperty(property: String, value: String): Boolean {
        when (property) {
            "name" -> return value.all(Char::isJavaIdentifierPart)
            "delimiter", "escape" -> return value.length == 1 || (value.matches(Regex("^%\\d+$")))
        }
        return true
    }
}