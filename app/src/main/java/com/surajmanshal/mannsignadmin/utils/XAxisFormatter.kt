package com.surajmanshal.mannsignadmin.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class XAxisFormatter : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        if(axis?.axisMinimum==value || axis?.axisMaximum==value) return ""
        return "${LocalDate.ofEpochDay(value.toLong()).apply {
            format(DateTimeFormatter.ofPattern("dd-MM"))
        }}"
    }
}