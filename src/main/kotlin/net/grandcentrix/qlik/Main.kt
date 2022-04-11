package net.grandcentrix.qlik

import net.grandcentrix.qlik.exporter.ExcelExporter
import net.grandcentrix.qlik.importer.WorkLogAnalyzer
import net.grandcentrix.qlik.importer.WorkLogImporter
import net.grandcentrix.qlik.importer.WorkLogCollector
import net.grandcentrix.qlik.model.Extensions
import net.grandcentrix.qlik.rules.EveryWorkDayShouldBeBooked
import net.grandcentrix.qlik.rules.TotalWorkShouldBeGreaterOrEqualOne
import java.nio.file.Path

class Main {
}

fun main() {
    val files = WorkLogCollector.collect(Path.of("/Users/oschmitz/Downloads"))

    val importer = WorkLogImporter()
    val logs = importer.importFile(files)
    val analyzer = WorkLogAnalyzer(listOf(EveryWorkDayShouldBeBooked(), TotalWorkShouldBeGreaterOrEqualOne()))
    val report = analyzer.analyze(logs)

    ExcelExporter().export(Path.of("/Users/oschmitz/Downloads/report.${Extensions.Excel.XLSX.value}"), report)
}