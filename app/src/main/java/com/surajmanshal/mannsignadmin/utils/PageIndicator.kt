package com.surajmanshal.mannsignadmin.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsignadmin.R
import kotlinx.coroutines.NonDisposableHandle.parent

class PageIndicator : RecyclerView.ItemDecoration() {

    private var paintStroke: Paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f
        color = R.color.authButtonColor
    }

    private var itemsCount = 0
    fun setItemsCount(n : Int){
        itemsCount = n
    }

    private val paintFill = Paint().apply {
        style = Paint.Style.FILL
        color = R.color.authButtonColor
    }

    // save the center coordinates of all indicators
    private val indicators = mutableListOf<Pair<Float, Float>>()

    private val indicatorRadius = 8f
    private val indicatorPadding = 4f

    private var activeIndicator = 0
    private var isInitialized = false

    // create three indicators for three slides
    override fun onDrawOver(canvas: Canvas,
                            parent: RecyclerView,
                            state: RecyclerView.State) {

        /*if(!isInitialized) {
            setupIndicators(parent)
        }*/

        // draw three indicators with stroke style
        createIndicators(parent,canvas)
    }

    fun createIndicators(parent: RecyclerView,canvas: Canvas){
        parent.adapter?.let {
            setupIndicators(parent)
            with(canvas) {
                for(i in 0 until itemsCount)
                    drawCircle(indicators[i].first, indicators[i].second)
            }

            val visibleItem = (parent.layoutManager as LinearLayoutManager)
                .findFirstCompletelyVisibleItemPosition()

            if(visibleItem >= 0) {
                activeIndicator = visibleItem
            }

            // paint over the needed circle
            for(i in 0 until itemsCount){
                if(activeIndicator==i)
                    canvas.drawCircle(indicators[i].first, indicators[i].second, true)
            }
        }
    }

    private fun Canvas.drawCircle(x: Float, y: Float, isFill: Boolean = false) {
        drawCircle(x, y, indicatorRadius, if(isFill) paintFill else paintStroke)
    }

    private fun setupIndicators(recyclerView: RecyclerView) {
        isInitialized = true

        val indicatorTotalWidth = indicatorRadius * 6 + indicatorPadding
        val indicatorPosX = (recyclerView.width - indicatorTotalWidth) / 2f
        val indicatorPosY = recyclerView.height - (indicatorRadius * 6 / 2f)

        if(itemsCount>0) indicators.add(indicatorPosX to indicatorPosY)
        for(i in 1 until itemsCount)
            indicators.add((indicatorPosX + indicatorRadius * (i*5)) to indicatorPosY)
    }
}
