package net.grandcentrix.qlik.importer

import net.grandcentrix.qlik.model.Extensions
import java.nio.file.Files
import java.nio.file.Path

object WorkLogCollector {

    fun collect(root: Path):List<Path> {
        return Files.walk(root).filter{ it.toFile().extension == Extensions.Excel.XLSX.value}.toList()
    }
}