package net.grandcentrix.qlik.importer

import mu.KotlinLogging
import net.grandcentrix.qlik.model.*
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.net.URI
import java.nio.file.Path
import java.util.Spliterator
import java.util.Spliterators
import java.util.stream.StreamSupport

class WorkLogImporter {

    private val log = KotlinLogging.logger{}

    fun importFile( paths: List<Path>): List<WorkLog> {
        val result = mutableListOf<WorkLog>()

        paths.forEach { result.addAll(this.importFile(it)) }

        return result.toList()
    }

    private fun importFile(path: Path): List<WorkLog> {
        check (path.toFile().extension == Extensions.Excel.XLSX.value)

        log.debug { "Importing $path ..." }
        val workbook = XSSFWorkbook(path.toFile())

        if(workbook.numberOfSheets < 1) {
            log.debug { "No sheets found" }
            return emptyList()
        }

        log.debug { "Number of sheets ${workbook.numberOfSheets}" }
        val sheet = workbook.getSheetAt(0)

        if(!isWoklog(sheet)) {
            log.debug { "Omitting invalid file $path" }
            return emptyList()
        }

        val logs = mutableListOf<WorkLog>()
        for((index, row) in sheet.withIndex()) {

            if(index == 0) continue //omit header
            if(ColumnMapping.STARTED.extract(row).cellType == CellType.STRING) continue // omit invalid logs

            logs.add(parse(row))
        }

        return logs.toList()
    }

    private fun isWoklog(sheet: Sheet): Boolean {
        val row = sheet.getRow(0)
        val expectedHeaders = listOf<String>("Team Member", "Key", "Summary", "Billable", "Non-billable", "Worklog started", "Comment")
        val actualHeaders = StreamSupport.stream(Spliterators.spliteratorUnknownSize(row.cellIterator(), Spliterator.ORDERED), false).map { it.stringCellValue.replace("[d]","").trim() }.toList()

        return expectedHeaders.toTypedArray() contentEquals  actualHeaders.toTypedArray()
    }

    private fun parse(row: Row): WorkLog {
        val name = ColumnMapping.NAME.extract(row).stringCellValue
        val key = ColumnMapping.KEY.extract(row).stringCellValue
        val summary = ColumnMapping.SUMMARY.extract(row).stringCellValue
        val comment = ColumnMapping.COMMENT.extract(row).stringCellValue

        val started = ColumnMapping.STARTED.extract(row).dateCellValue
        val billable = ColumnMapping.BILLABLE.extract(row).numericCellValue
        val nonBillable = ColumnMapping.NON_BILLABLE.extract(row).numericCellValue

        return WorkLog(
            TeamMember(Name(name)),
            URI(key),
            Summary(summary),
            Comment(comment),
            Work(started.toInstant(),
                billable,
                nonBillable
            ))
    }

}