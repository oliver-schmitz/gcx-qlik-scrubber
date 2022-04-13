package net.grandcentrix.qlik.rules

import mu.KotlinLogging
import net.grandcentrix.qlik.model.Work
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId

/**
 * Evaluates missing work logs. Omits Saturday and Sundays.
 */
class EveryWorkDayShouldBeBooked: Rule {

    private val log = KotlinLogging.logger {  }

    override fun eval(logs:List<Work>): List<EvaluationReport> {

        val daysLogged = logs.map { LocalDate.ofInstant(it.started, ZoneId.systemDefault()) }.toSet()
        val missingLogs = mutableListOf<LocalDate>()
        val startOfTheMonths = daysLogged.map { it.withDayOfMonth(1) }

        for(startOfTheMonth in startOfTheMonths){
            val today = LocalDate.now().plusDays(1)
            val delta = startOfTheMonth
                .datesUntil(today)
                .filter { it.dayOfWeek != DayOfWeek.SATURDAY }
                .filter { it.dayOfWeek != DayOfWeek.SUNDAY }
                .filter { !daysLogged.contains(it)}
                .toList()
            missingLogs.addAll(delta)
        }

        return missingLogs.map { EvaluationReport(it, MissingLogMessage()) }
    }

}