package inu.thebite.umul.fragment.dayFragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import inu.thebite.umul.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet

class ReportFragment1_1 : Fragment() {

    var myChildTotalCnt : Float = 200.0f
    var averageTotalCnt : Float = 300.0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_report_fragment1_1, container, false)
        var totalCntGraph : BarChart = view.findViewById(R.id.graph)

        initBarCHart(totalCntGraph)
        return view
    }

    private fun initBarCHart(barChart: BarChart) {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1f,myChildTotalCnt))
        entries.add(BarEntry(2f,averageTotalCnt))
        barChart.run {
            description.isEnabled = false
            setMaxVisibleValueCount(2)
            setPinchZoom(false)
            setDrawBarShadow(false)
            setDrawGridBackground(false)
            axisLeft.run {
                axisMaximum = if(myChildTotalCnt > averageTotalCnt){
                    myChildTotalCnt + 1f
                } else{
                    averageTotalCnt + 1f
                }
                println(axisMaximum)
                axisMinimum = 0f
                granularity = myChildTotalCnt
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

                textSize = 20f
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
        private val xLabel = arrayOf("우리아이", "평균")

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return xLabel.getOrNull(value.toInt()-1) ?: value.toString()
        }
    }



}