package net.grandcentrix.qlik.exporter

import mu.KotlinLogging
import net.grandcentrix.qlik.model.TeamMember
import net.grandcentrix.qlik.rules.EvaluationReport
import net.grandcentrix.qlik.rules.Message
import net.grandcentrix.qlik.rules.Severity
import net.grandcentrix.qlik.rules.SumMismatchMessage
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import java.nio.file.Path
import java.time.Instant
import java.util.*

class ExcelExporter {

    private val log = KotlinLogging.logger {  }

    fun export(path: Path, report: Map<TeamMember, List<EvaluationReport>>) {

        val workbook = XSSFWorkbook()
        val dateCellStyle = workbook.createCellStyle()
        dateCellStyle.dataFormat = workbook.creationHelper.createDataFormat().getFormat("dd.mm.yyyy")

        log.debug { "Export report to $path" }

        for((member, reports) in report) {
            log.debug { "Writing report for ${member.name.value}" }
            val sheet = workbook.createSheet(member.name.value)

            reports.sortedBy { it.date }

            this.createHeader(sheet.createRow(0))

            reports.forEachIndexed { index, evaluationReport ->
                val message = evaluationReport.message
                val row = sheet.createRow(index + 1)

                val dateCell = row.createCell(0)
                    dateCell.setCellValue(evaluationReport.date)
                    dateCell.cellStyle = dateCellStyle

                row.createCell(1).setCellValue(message.severity)
                row.createCell(2).setCellValue(message.text)

                if(message is SumMismatchMessage ) {
                    row.createCell(3).setCellValue(message.billable)
                    row.createCell(4).setCellValue(message.nonBillable)
                    row.createCell(5).setCellValue(message.sum)
                }
            }
        }
        log.debug { "Writing report file..." }
        val time = System.currentTimeMillis()
        workbook.write(FileOutputStream(path.toFile()))
        log.debug { "done in ${System.currentTimeMillis() - time} ms" }
    }

    private fun createHeader(row: Row){
        row.createCell(0).setCellValue("Date")
        row.createCell(1).setCellValue("Severity")
        row.createCell(2).setCellValue("Message")
        row.createCell(3).setCellValue("Billable")
        row.createCell(4).setCellValue("NonBillable")
        row.createCell(5).setCellValue("Sum")
    }

}

fun XSSFCell.setCellValue(instant: Instant) = this.setCellValue(Date.from(instant))
fun XSSFCell.setCellValue(severity: Severity) = this.setCellValue(severity.name)
fun XSSFCell.setCellValue(message: Message) = this.setCellValue(message.toString())

fun Severity.color() = when(this) {
    Severity.LOW -> IndexedColors.BRIGHT_GREEN.index
    Severity.MEDIUM -> IndexedColors.YELLOW.index
    Severity.HIGH -> IndexedColors.RED.index
}