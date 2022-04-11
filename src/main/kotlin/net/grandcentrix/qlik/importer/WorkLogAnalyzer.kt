package net.grandcentrix.qlik.importer

import mu.KotlinLogging
import net.grandcentrix.qlik.model.TeamMember
import net.grandcentrix.qlik.model.Work
import net.grandcentrix.qlik.model.WorkLog
import net.grandcentrix.qlik.rules.EvaluationReport
import net.grandcentrix.qlik.rules.EveryWorkDayShouldBeBooked
import net.grandcentrix.qlik.rules.Rule
import net.grandcentrix.qlik.rules.TotalWorkShouldBeGreaterOrEqualOne
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.TemporalAdjusters

class WorkLogAnalyzer(private val rules: List<Rule>) {

    private val log = KotlinLogging.logger{}

    fun analyze(logs: List<WorkLog>): Map<TeamMember,List<EvaluationReport>> {

        val report = mutableMapOf<TeamMember, List<EvaluationReport>>()
        log.debug { "Extract members from ${logs.size} entries" }

        val members = logs.map { it.member }.toSet()
        log.debug { "Found members: ${members.map { it.name.value }}" }

        for(member in members) {
            log.debug { "Extracting work for ${member.name.value}" }
            val work = logs.filter { it.member == member }.map { it.work }

            val reports = mutableListOf<EvaluationReport>()
            rules.forEach {
                val entries = it.eval(work)
                log.debug { "${it.javaClass.name} Found ${entries.size} issues" }
                reports.addAll(entries)
            }

            report[member] = reports
            log.debug { "Found ${reports.size} issues for ${member.name.value}" }
        }

        return report
    }
}