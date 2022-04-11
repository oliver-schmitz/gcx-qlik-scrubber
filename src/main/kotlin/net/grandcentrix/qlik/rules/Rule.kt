package net.grandcentrix.qlik.rules

import net.grandcentrix.qlik.model.Work
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.math.abs

interface Rule {
    fun eval(logs: List<Work>):List<EvaluationReport>
}

data class EvaluationReport(
    val date: Instant,
    val message: Message)
{
    constructor(date: LocalDate, message: Message):
            this(date.atStartOfDay().toInstant(ZoneOffset.UTC), message)
}

enum class Severity(number: Int) {
    LOW(1),
    MEDIUM(2),
    HIGH(3)
}

abstract class Message(open val severity: Severity, open val text: String)

data class MissingLogMessage(
    override val severity: Severity = Severity.HIGH,
    override val text: String = "Missing Work Log"): Message (
    severity, text
)

data class SumMismatchMessage(
    val billable: Double,
    val nonBillable: Double,
    override val severity: Severity = Severity.MEDIUM,
    override val text: String = "Quota does not match",
    val sum: Double = abs(billable) + abs(nonBillable)
):Message(severity, text)

