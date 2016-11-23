package net.kimleo.rec

import net.kimleo.rec.api.runscript
import net.kimleo.rec.init.Initializer
import net.kimleo.rec.loader.strategy.DefaultLoadingStrategy
import net.kimleo.rec.rule.RuleLoader
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        runOverPath(".")
    }

    when (args[0]) {
        "init" -> {
            if(args.size < 2) { exit("You need provide a file to initialize!") }
            val file = args[1]

            if (!Files.exists(Paths.get(file))) { exit("File <$file> cannot be found!") }

            val properties = linkedMapOf<String, String>()
            args.drop(2).forEach {
                if (!it.contains("=")) {  exit("Unexpected parameter format, should be <param>=<value>") }
                val parts = it.split("=")
                properties[parts[0].trim()] = parts[1].trim()
            }

            Initializer(file, properties).init()
        }
        "script" -> {
            if (args.size !=2) {
                exit("You need only to provide a script file.")
            }

            val file = args[1]
            if (!File(file).exists()) { exit("File <$file> cannot be found!") }
            runscript(file)
        }
        else -> {
            if (args.size != 1 || !Files.exists(Paths.get(args[0]))) {
                exit("You should run with a folder contains rec files!")
            }
            runOverPath(args[0])
        }
    }
}

private fun runOverPath(basePath: String) {

    val repo = DefaultLoadingStrategy.repo(basePath)

    val rules = lines(Paths.get(basePath, "default.rule").toString())
    RuleLoader().load(rules).map {
        it.runOn(repo)
    }.filter {
        !it.first
    }.forEach {
        it.second.forEach {
            println(it.details)
        }
    }
}

fun lines(file: String): List<String> {
    val reader = BufferedReader(InputStreamReader(FileInputStream(File(file))))

    val lines = reader.readLines()
    reader.close()
    return lines
}