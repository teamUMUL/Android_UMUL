package inu.thebite.umul.fragment.dayFragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import inu.thebite.umul.adapter.decoration.CustomBarChartRender
import inu.thebite.umul.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet


class ReportFragment1_3 : Fragment() {
    var myChildAvgABite : Float = 40.0f
    var averageAvgABite : Float = 32.0f
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_report_fragment1_3, container, false,)
        var avgABiteGraph : BarChart = view.findViewById(R.id.graph3)
        avgABiteGraph.setDrawValueAboveBar(false)
        val barChartRender =
            CustomBarChartRender(avgABiteGraph, avgABiteGraph.animator, avgABiteGraph.viewPortHandler)
        barChartRender.setRadius(30)
        avgABiteGraph.renderer = barChartRender
        avgABiteGraph.setDrawValueAboveBar(false)
        initBarCHart(avgABiteGraph)
        return view
    }


    private fun initBarCHart(barChart: BarChart) {

        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1f,myChildAvgABite))
        entries.add(BarEntry(2f,averageAvgABite))
        barChart.run {
            description.isEnabled = false
            setMaxVisibleValueCount(10)
            setPinchZoom(false)
            setDrawBarShadow(false)
            setDrawGridBackground(false)
            axisLeft.run {
                axisMaximum = if(myChildAvgABite > averageAvgABite){
                    myChildAvgABite + 1f
                } else{
                    averageAvgABite + 1f
                }

                axisMinimum = 0f
                val ll = LimitLine(myChildAvgABite)
                ll.lineWidth = 3f
                ll.enableDashedLine(10f,10f,2f)
                ll.lineColor =
                    ContextCompat.getColor(context, R.color.gray)
                removeAllLimitLines()
                addLimitLine(ll)
                setDrawGridLines(false)
                setDrawLimitLinesBehindData(false)
                setDrawLabels(false)
                setDrawAxisLine(false)
                setDrawZeroLine(true)

                zeroLineWidth = 4f

                zeroLineColor =
                    ContextCompat.getColor(context, R.color.gray)
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
        barChart.extraTopOffset = 3f

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
            return value.toInt().toString()+"회"
        }
    }
    inner class MyValueFormatter : ValueFormatter(){

        override fun getFormattedValue(value: Float): String {
            return value.toInt().toString()+"회"
        }
    }

}