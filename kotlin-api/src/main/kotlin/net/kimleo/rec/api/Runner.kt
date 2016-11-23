package net.kimleo.rec.api

import net.kimleo.rec.exit
import java.io.File
import java.io.InputStream


class Dummy {}

private val COMPILER_CLASS_FQN = "org.jetbrains.kotlin.cli.jvm.K2JVMCompiler"

private val javaExecutable = File( File(System.getProperty("java.home"), "bin"), "java")
private val jarFile = File(getContainedJarFile(Dummy::class.java)).absolutePath

fun runscript(file: String) {
    val script = File(file)
    if (!script.exists()) {
        exit("Expect $file but not found")
    }
    runCompiler("-cp", jarFile, "-script", file, basePath = script.absoluteFile.parentFile)
}

private fun runCompiler(vararg arguments: String, basePath: File): Pair<String, Int> {
    val cmd = listOf(javaExecutable.absolutePath,
            "-Djava.awt.headless=true",
            "-cp",
            jarFile,
            COMPILER_CLASS_FQN) +
            arguments
    val proc = createProcess(cmd, basePath)
    return readOutput(proc)
}

private fun createProcess(cmd: List<String>, projectDir: File): Process {
    val builder = ProcessBuilder(cmd)
    builder.directory(projectDir)
    return builder.start()
}

private fun getContainedJarFile(cls: Class<Dummy>) = cls.protectionDomain.codeSource.location.toURI()

private fun readOutput(process: Process): Pair<String, Int> {

    val errorFile = File("error.log")

    fun InputStream.readFully(): String {
        val text = reader().readText()
        close()
        return text
    }

    val stdout = process.inputStream!!.readFully()
    System.out.print(stdout)
    val stderr = process.errorStream!!.readFully()
    errorFile.appendText(stderr)

    val result = process.waitFor()
    return stdout to result
}