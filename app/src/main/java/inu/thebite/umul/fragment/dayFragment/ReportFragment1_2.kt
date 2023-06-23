package inu.thebite.umul.fragment.dayFragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import inu.thebite.umul.adapter.decoration.CustomBarChartRender
import inu.thebite.umul.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet


class ReportFragment1_2 : Fragment() {
    var myChildTotalTime : Float = 2870.0f
    var averageTotalTime : Float = 3300.0f
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_report_fragment1_2, container, false,)
        var totalTimeGraph : BarChart = view.findViewById(R.id.graph2)
        totalTimeGraph.setDrawValueAboveBar(false)
        val barChartRender =
            CustomBarChartRender(totalTimeGraph, totalTimeGraph.animator, totalTimeGraph.viewPortHandler)
        barChartRender.setRadius(30)
        totalTimeGraph.renderer = barChartRender
        initBarCHart(totalTimeGraph)
        return view
    }


    private fun initBarCHart(barChart: BarChart) {

        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1f,myChildTotalTime))
        entries.add(BarEntry(2f,averageTotalTime))
        barChart.run {
            description.isEnabled = false
            setMaxVisibleValueCount(10)
            setPinchZoom(false)
            setDrawBarShadow(false)
            setDrawGridBackground(false)
            axisLeft.run {
                axisMaximum = if(myChildTotalTime > averageTotalTime){
                    myChildTotalTime + 1f
                } else{
                    averageTotalTime + 1f
                }

                axisMinimum = 0f
                granularity = myChildTotalTime
                setDrawLabels(true)
                setDrawGridLinesBehindData(false)
                setDrawGridLines(true)
                setDrawAxisLine(false)
                setDrawZeroLine(true)
                enableGridDashedLine(10f,10f,2f)
                gridLineWidth = 3f
                zeroLineWidth = 4f
                gridColor =
                    ContextCompat.getColor(context, R.color.gray)
                textColor =
                    ContextCompat.getColor(context, R.color.black)
                zeroLineColor =
                    ContextCompat.getColor(context, R.color.gray)
                valueFormatter = MyLeftAxisFormatter()
                textSize = 10f
            }
            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawAxisLine(false)
                setDrawGridLines(false)

                textColor =
                    ContextCompat.getColor(context, R.color.black)
                textSize = 13f
                valueFormatter = MyXAxisFormatter()
            }
            axisRight.isEnabled = false
            setTouchEnabled(false)
            animateY(1000)
            legend.isEnabled = false

        }

        barChart.extraBottomOffset = 3f


        var set = BarDataSet(entries,"DataSet")
        set.setColors(
            Color.rgb(0, 226, 126), Color.rgb(255, 167, 167)
        )
        set.setDrawValues(true)
        set.valueFormatter = MyValueFormatter()
        set.valueTextSize = 10f
        val dataSet : ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(set)

        val data = BarData(dataSet)
        data.barWidth = 0.6f
        barChart.run {
            this.data = data
            setFitBars(true)
            invalidate()
        }
    }

    inner class MyXAxisFormatter : ValueFormatter(){
        private val xLabel = arrayOf("우리아이", "비만군")

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return xLabel.getOrNull(value.toInt()-1) ?: value.toString()
        }
    }
    inner class MyLeftAxisFormatter : ValueFormatter(){

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            var totalSecond = value.toInt()
            var second = totalSecond % 60
            var minute = totalSecond / 60 % 60
            return if(minute == 0){
                second.toString()+"초"
                }  else {
                minute.toString() + "분 " + second.toString() + "초"
                }
        }
    }

    inner class MyValueFormatter : ValueFormatter(){

        override fun getFormattedValue(value: Float): String {
            var totalSecond = value.toInt()
            var second = totalSecond % 60
            var minute = totalSecond / 60 % 60
            return if(minute == 0){
                second.toString()+"초"
            }  else {
                minute.toString() + "분 " + second.toString() + "초"
            }
        }
    }

}