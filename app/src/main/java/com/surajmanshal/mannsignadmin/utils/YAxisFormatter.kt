package com.surajmanshal.mannsignadmin.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class YAxisFormatter(private val unit : String) : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return if(value == axis?.axisMaximum) ""
        else if(value<1000) "${value.toInt()} $unit"
        else "${(value/1000).toInt()}K $unit"
    }
}