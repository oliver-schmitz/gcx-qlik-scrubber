package net.grandcentrix.qlik.model

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import java.net.URI
import java.time.Instant

enum class ColumnMapping(private val index: Int) {
    NAME(0),
    KEY(1),
    SUMMARY(2),
    BILLABLE(3),
    NON_BILLABLE(4),
    STARTED(5),
    COMMENT(6);

    fun extract(row: Row): Cell = row.getCell(index)
}

data class WorkLog(val member: TeamMember, val key: URI, val summary: Summary? = null, val comment: Comment? = null, val work: Work)

data class Work(val started: Instant, var billable: Double = 0.0, var nonBillable: Double = 0.0)

data class TeamMember(val name: Name)

@JvmInline
value class Name(val value: String)

@JvmInline
value class Summary(val value: String)

@JvmInline
value class Comment(val value: String)
