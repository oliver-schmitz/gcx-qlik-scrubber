package net.grandcentrix.qlik.rules

import net.grandcentrix.qlik.model.Work
import kotlin.math.abs

/**
 * Checks if the sum of billable and non-billable work is greater or equal 1 for a day.
 */
class TotalWorkShouldBeGreaterOrEqualOne:Rule {

    override fun eval(logs: List<Work>): List<EvaluationReport> {
        val report = mutableListOf<EvaluationReport>()

        for (log in logs) {
            val sum = abs(log.billable) + abs(log.nonBillable)

            if (sum < 1)
                report.add(EvaluationReport(log.started, SumMismatchMessage(log.billable, log.nonBillable, Severity.MEDIUM, "Quota is less than 1")))
            if(sum > 1)
                report.add(EvaluationReport(log.started, SumMismatchMessage(log.billable, log.nonBillable, Severity.LOW, "Quota is greater than 1")))
        }
        return report.toList()
    }

}