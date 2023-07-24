package inu.thebite.umul.fragment.dayFragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
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
import inu.thebite.umul.databinding.FragmentReportFragment11Binding
import inu.thebite.umul.databinding.FragmentReportFragment12Binding
import inu.thebite.umul.model.DailyReportTotalTimeResponse
import inu.thebite.umul.retrofit.RetrofitAPI
import retrofit2.Call
import retrofit2.Response
import java.time.LocalDate


/**
 * Report 총 식사시간
 */
class ReportFragment1_2 : Fragment() {
    var myChildTotalTime : Float = 0f
    var averageTotalTime : Float = 3300.0f
    private lateinit var childName: String
    private lateinit var memberNumber : String
    private lateinit var binding : FragmentReportFragment12Binding
    var feedback2 = MutableLiveData("피드백 내용")
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        childName = getChildNameFromPref()
        memberNumber = getMemberNumberFromPref()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report_fragment1_2, container, false)
        binding.reportFragment12 = this
        binding.lifecycleOwner = this
        var view = binding.root
        var totalTimeGraph : BarChart = view.findViewById(R.id.graph2)
        totalTimeGraph.setDrawValueAboveBar(false)
        val barChartRender =
            CustomBarChartRender(totalTimeGraph, totalTimeGraph.animator, totalTimeGraph.viewPortHandler)
        barChartRender.setRadius(30)
        totalTimeGraph.renderer = barChartRender

        /**
         * Connection to Server
         * childrenId -> 홈 화면에서 자녀 설정 후 id값 넘겨주기
         * 우선은 default 1로 설정
         */
        RetrofitAPI.emgMedService.getDailyReportWithTotalTime(childName, memberNumber, LocalDate.now().toString())
            .enqueue(object : retrofit2.Callback<DailyReportTotalTimeResponse> {
                override fun onResponse(
                    call: Call<DailyReportTotalTimeResponse>,
                    response: Response<DailyReportTotalTimeResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.d("총 식사시간 가져오기 성공", "$result")
                        initBarCHart(totalTimeGraph, result!!.totalTime)
                        initFeedback(response.body()!!.feedback)
                    }
                }

                override fun onFailure(call: Call<DailyReportTotalTimeResponse>, t: Throwable) {
                    Log.d("총 식사시간 가져오기 실패", t.message.toString())
                }
            })

        return view
    }


    private fun initBarCHart(barChart: BarChart, childTime: Float) {

        Log.d("childCnt = ", childTime.toString())

        myChildTotalTime = childTime

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

                val ll = LimitLine(myChildTotalTime)
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
    private fun initFeedback(feedbackText : String){
        feedback2.value = feedbackText
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

    fun timeFormatter(totalSecond : Float): String {
        var totalSeconds = totalSecond.toInt()
        var second = totalSeconds % 60
        var minute = totalSeconds / 60 % 60
        return if(minute == 0){
            second.toString()+"초"
        }  else {
            minute.toString() + "분 " + second.toString() + "초"
        }
    }

    fun getMemberNumberFromPref(): String {
        val pref: SharedPreferences = requireContext().getSharedPreferences("MemberNumber", Context.MODE_PRIVATE)
        val memberNumber = pref.getString("MemberNumber", "010-0000-0000").toString()

        return memberNumber
    }

    fun getChildNameFromPref(): String {
        val pref: SharedPreferences = requireContext().getSharedPreferences("selectedChild", Context.MODE_PRIVATE)
        val childName = pref.getString("selectedChild", "홍길동").toString()

        return childName
    }

}