package com.surajmanshal.mannsignadmin.utils

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.surajmanshal.mannsignadmin.R


class GraphMarker(context: Context, layoutResId : Int,val suffix : String) : MarkerView(context,layoutResId) {

private val tvPrice = findViewById<TextView>(R.id.tvTagValue)
    override fun refreshContent(entry: Entry?, highlight: Highlight?) {
        val value = entry?.y?.toDouble() ?: 0.0
        tvPrice.text = if(value.toString().length > 8) value.toString().substring(0,7) + suffix else "$value $suffix"
        super.refreshContent(entry, highlight)
    }

    override fun getOffsetForDrawingAtPoint(xpos: Float, ypos: Float): MPPointF {
        return MPPointF(-width / 2f, -height - 10f)
    }
}