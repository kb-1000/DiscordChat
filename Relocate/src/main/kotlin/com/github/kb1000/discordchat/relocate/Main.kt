package com.github.kb1000.discordchat.relocate

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes.ASM7
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.Remapper
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

data class Options(var output: String? = null, var input: MutableList<String> = mutableListOf())

fun main(vararg argv: String) {
    val options = Options()
    val iterator = listOf(*argv).iterator()
    for (opt in iterator) {
        if (opt[0] == '-') {
            when (opt) {
                "-o" -> {
                    if (options.output != null) {
                        throw IllegalArgumentException("Specified -o more than one time")
                    }
                    options.output = iterator.next()
                }
                else -> {
                    throw IllegalArgumentException("Unknown argument: $opt")
                }
            }
        } else {
            options.input.add(opt)
        }
    }
    val output = Paths.get(options.output ?: throw IllegalArgumentException("Missing -o (output) argument"))
    for (input in options.input) {
        val inputFile = Paths.get(input)
        if (Files.isDirectory(inputFile)) {
            for (file in Files.walk(inputFile)) {
                if (Files.isRegularFile(file)) {
                    if (file.toString().endsWith(".class")) {
                        processFile(output, file)
                    }
                }
            }
        }
    }
}

private fun processFile(output: Path, file: Path) {
    val classReader = ClassReader(Files.newInputStream(file, StandardOpenOption.READ))
    val classWriter = ClassWriter(classReader, 0)
    lateinit var className: String
    // source code obfuscation at its best 😂
    // FIXME: rewrites even external class references like FML or Java standard library
    classReader.accept(object : ClassVisitor(ASM7, ClassRemapper(classWriter, object : Remapper() {
        override fun mapPackageName(name: String) = when {
            name.startsWith("com.github.kb1000.discordchat") -> name
            name.startsWith("java") -> name
            name.startsWith("org.objectweb.asm") -> name
            name.startsWith("net.minecraft") -> name
            else -> "com.github.kb1000.discordchat.fatjar.$name"
        }

        override fun map(internalName: String) = when {
            internalName.startsWith("com/github/kb1000/discordchat") -> internalName
            internalName.startsWith("java") -> internalName
            internalName.startsWith("org/objectweb/asm") -> internalName
            internalName.startsWith("net/minecraft") -> internalName //
            else -> "com/github/kb1000/discordchat/fatjar/$internalName"
        }
    })) {
        override fun visit(
            version: Int,
            access: Int,
            name: String,
            signature: String?,
            superName: String?,
            interfaces: Array<String>
        ) {
            className = name
            super.visit(version, access, name, signature, superName, interfaces)
        }
    }, 0)
    val outFile = output.resolve("$className.class")
    Files.createDirectories(outFile.parent)
    Files.write(outFile, classWriter.toByteArray())
}
