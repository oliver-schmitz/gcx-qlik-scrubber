package net.grandcentrix.qlik.rules

import assertk.assertThat
import assertk.assertions.doesNotContain
import assertk.assertions.isSuccess
import net.grandcentrix.qlik.model.Work
import org.junit.jupiter.api.Test
import java.time.*
import java.time.temporal.TemporalAdjusters

internal class EveryWorkDayShouldBeBookedTest{

    @Test
    fun `Logged days Should not be in the report`() {
        val date = LocalDate.of(2022, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC)
        val logs = listOf<Work>(Work(date, 0.0, 0.0))
        EveryWorkDayShouldBeBooked().eval(logs).forEach { println(it) }
        assertThat { EveryWorkDayShouldBeBooked().eval(logs) }.isSuccess().doesNotContain(date)
    }

    @Test
    fun `Saturday and Sundays should not be in the report`(){
        val date = LocalDate.of(2022, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC)
        val logs = listOf<Work>(Work(date, 0.0, 0.0))

        val startOfMonth = LocalDate.ofInstant(date, ZoneId.systemDefault())
        val lastDayOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1)
        val allSaturdayAndSundays = startOfMonth.datesUntil(lastDayOfMonth).filter{ it. dayOfWeek == DayOfWeek.SATURDAY || it.dayOfWeek == DayOfWeek.SUNDAY}.toList()

        assertThat { EveryWorkDayShouldBeBooked().eval(logs) }.isSuccess().doesNotContain(allSaturdayAndSundays)
    }


}