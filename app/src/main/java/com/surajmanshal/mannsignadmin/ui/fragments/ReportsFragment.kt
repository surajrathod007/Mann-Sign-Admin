package com.surajmanshal.mannsignadmin.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.databinding.FragmentReportsBinding
import com.surajmanshal.mannsignadmin.ui.activity.ReportDetailsActivity
import com.surajmanshal.mannsignadmin.utils.XAxisFormatter
import com.surajmanshal.mannsignadmin.utils.YAxisFormatter
import com.surajmanshal.mannsignadmin.viewmodel.StatsViewModel


class ReportsFragment : Fragment() {

    lateinit var binding: FragmentReportsBinding
    lateinit var vm: StatsViewModel
    lateinit var lineChart: LineChart
    lateinit var activeDataSet: LineDataSet
    lateinit var pieChart : PieChart
    lateinit var pieDataSet: PieDataSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()

        vm.getAllOrders()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_reports, container, false)
        binding = FragmentReportsBinding.bind(view)

        vm = ViewModelProvider(requireActivity()).get(StatsViewModel::class.java)

        vm.setupViewModelDataMembers()

        vm.allOrders.observe(viewLifecycleOwner) {
            binding.txtTotalOrders.text = it.size.toString()
        }

        vm.transactionItems.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.txtTotalTransactions.text = it.size.toString()
                var t = 0.0f
                it.forEach {
                    t += it.transaction.amount
                }
                binding.txtTotalEarnings.text = "₹" + t
            }
            // LineChart setup
            lineChart = binding.transactionsLineGraphLayout.graph
            setupChartView()
            val series = createGraphSeries(it
                .map {
                    Pair(it.transaction.date.toEpochDay().toFloat()
                    ,it.transaction.amount)
                })
            activeDataSet = createLineDataSet(series, "")
            lineChart.data = LineData(activeDataSet)
            // PieChart setup
            pieChart = binding.transactionsPieGraphLayout.graph
            pieChart.data = PieData(PieDataSet(it
                .map {
                    PieEntry(it.transaction.amount)
                },""))
        }

        vm.allUsers.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.txtTotalUsers.text = it.size.toString()
            }
        }

        vm.products.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                //its total product
                binding.txtTotalInvoice.text = it.size.toString()
            }
        }

        vm.orderSize.observe(viewLifecycleOwner){
            //Toast.makeText(requireContext(),"Size is $it",Toast.LENGTH_LONG).show()
        }

        vm.serverResponse.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
        }

        with(binding) {

            transactionsLineGraphLayout.graphLabel.text = "Transactions"
            transactionsPieGraphLayout.graphLabel.text = "Transaction Modes"
            //card click listeners
            cardTransaction.setOnClickListener {
                val i = Intent(it.context, ReportDetailsActivity::class.java)
                i.putExtra("index", 1)
                startActivity(i)
            }
            cardOrders.setOnClickListener {
                val i = Intent(it.context, ReportDetailsActivity::class.java)
                i.putExtra("index", 0)
                startActivity(i)
            }
            cardProducts.setOnClickListener {
                val i = Intent(it.context, ReportDetailsActivity::class.java)
                i.putExtra("index", 2)
                startActivity(i)
            }
            cardUsers.setOnClickListener {
                val i = Intent(it.context, ReportDetailsActivity::class.java)
                i.putExtra("index", 3)
                startActivity(i)
            }
        }
        return binding.root
    }

    private fun setupChartView() {
        lineChart.apply {
            description.isEnabled = false
            xAxis.labelRotationAngle = 0f
            axisRight.isEnabled = false
            axisLeft.axisMinimum = 0f
            axisLeft.valueFormatter = YAxisFormatter("")
            xAxis.valueFormatter = XAxisFormatter()

            setTouchEnabled(true)
            animateX(500, Easing.EaseInOutSine)
            // Setting up the marker on Tap
//            val markerView = GraphMarker(requireContext(), R.layout.graph_marker, "INR")
//            marker = markerView
        }
    }

    fun setupDatasetTheme(dataSet: LineDataSet) {
        dataSet.setDrawValues(false)
        dataSet.lineWidth = 0f
        dataSet.setDrawCircles(false)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.fillAlpha = 255
        dataSet.setDrawFilled(true)
        dataSet.formLineWidth = 0f
        dataSet.formSize = 0f
        dataSet.highLightColor = resources.getColor(R.color.teal_200)
        dataSet.highlightLineWidth = 2f
        dataSet.setDrawHorizontalHighlightIndicator(false)
    }

    private fun createLineDataSet(series: ArrayList<Entry>, name: String) =
        LineDataSet(series, name).apply {

        }
    private fun createGraphSeries(priceList: List<Pair<Float, Float>>): ArrayList<Entry> {
        val entries = ArrayList<Entry>()
        for (i in (priceList.indices)) {
            println(priceList[i].first)
            entries.add(Entry(i.toFloat(), priceList[i].second)) // todo : change entry of x by timestamp
        }
        return entries
    }
}