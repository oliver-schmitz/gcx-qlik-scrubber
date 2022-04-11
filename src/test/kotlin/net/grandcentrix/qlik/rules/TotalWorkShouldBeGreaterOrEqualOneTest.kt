package net.grandcentrix.qlik.rules

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isSuccess
import assertk.assertions.matchesPredicate
import net.grandcentrix.qlik.model.Work
import org.junit.jupiter.api.Test
import java.time.Instant

internal class TotalWorkShouldBeGreaterOrEqualOneTest {

    @Test
    fun `Sum of billable an non-billable is one`() {
        val work = listOf<Work>(Work(Instant.now(), .5, .5))
        val rule = TotalWorkShouldBeGreaterOrEqualOne()
        assertThat { rule.eval(work) }.isSuccess().isEmpty()
    }

    @Test
    fun `Sum of billable and non-billable is greater than one`() {
        val now = Instant.now()
        val work = listOf<Work>(Work(now, .6, .5))
        val rule = TotalWorkShouldBeGreaterOrEqualOne()

        assertThat { rule.eval(work)}
            .isSuccess()
            .transform { it[0] }
            .matchesPredicate {
                it.date == now &&
                it.message.severity == Severity.LOW &&
                it.message.text == "Quota is greater than 1"
            }
    }

    @Test
    fun `Sum of billable and non-billable is less than one`() {
        val now = Instant.now()
        val work = listOf<Work>(Work(now, .4, .5))
        val rule = TotalWorkShouldBeGreaterOrEqualOne()

        assertThat { rule.eval(work)}.isSuccess()
            .transform { it[0] }
            .matchesPredicate {
                it.date == now &&
                it.message.severity == Severity.MEDIUM &&
                it.message.text == "Quota is less than 1"
            }
    }
}