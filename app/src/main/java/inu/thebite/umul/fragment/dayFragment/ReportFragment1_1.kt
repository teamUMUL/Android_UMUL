package inu.thebite.umul.fragment.dayFragment

import CalendarAdapter
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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import inu.thebite.umul.adapter.decoration.CustomBarChartRender
import inu.thebite.umul.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import inu.thebite.umul.databinding.FragmentReport1Binding
import inu.thebite.umul.databinding.FragmentReportFragment11Binding
import inu.thebite.umul.fragment.bottomNavFragment.ReportFragment
import inu.thebite.umul.model.DailyReportTotalCountResponse
import inu.thebite.umul.retrofit.RetrofitAPI
import inu.thebite.umul.viewmodel.ReportViewModel
import retrofit2.Call
import retrofit2.Response
import java.time.LocalDate

/**
 * Report 총 저작횟수
 */
class ReportFragment1_1 : Fragment() {

    private lateinit var viewModel: ReportViewModel

    var myChildTotalCnt : Float = 0f
    var fatTotalCnt : Float = 140.0f
    private lateinit var childName: String
    private lateinit var memberNumber : String
    private lateinit var binding : FragmentReportFragment11Binding
    var feedback1 = MutableLiveData("피드백 내용")
    private lateinit var gender : String
    private lateinit var totalCntGraph : BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ReportViewModel::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        childName = getChildNameFromPref()
        memberNumber = getMemberNumberFromPref()
        Toast.makeText(activity, childName, Toast.LENGTH_SHORT).show();
        gender = getChildGenderFromPref()
        if(gender == "M"){
            fatTotalCnt = 80.0f
        }
        else if(gender == "F"){
            fatTotalCnt = 320.0f
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report_fragment1_1, container, false)
        binding.reportFragment11 = this
        binding.lifecycleOwner = this
        var view = binding.root
        var totalCntGraph : BarChart = view.findViewById(R.id.graph1)
        totalCntGraph.setDrawValueAboveBar(false)
        val barChartRender =
            CustomBarChartRender(totalCntGraph, totalCntGraph.animator, totalCntGraph.viewPortHandler)
        barChartRender.setRadius(30)
        totalCntGraph.renderer = barChartRender
        totalCntGraph.setDrawValueAboveBar(false)
        RetrofitAPI.emgMedService.getDailyReportWithTotalCount(childName, LocalDate.now().toString())
                .enqueue(object : retrofit2.Callback<DailyReportTotalCountResponse> {
                    override fun onResponse(
                        call: Call<DailyReportTotalCountResponse>,
                        response: Response<DailyReportTotalCountResponse>
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            Log.d("총 저작횟수 가져오기 성공", "$result")
                            initBarCHart(totalCntGraph, response.body()!!.totalCount)
                            initFeedback(response.body()!!.feedback)
                        }
                    }

                    override fun onFailure(call: Call<DailyReportTotalCountResponse>, t: Throwable) {
                        Log.d("총 저작횟수 가져오기 실패", t.message.toString())
                    }
                })

        Log.d("reportFrag1", "들어옴!")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        totalCntGraph = view.findViewById(R.id.graph1)
        viewModel.touchedDay.observe(viewLifecycleOwner, Observer { newData ->
            val dayByString = newData.toString().takeLast(2)
            val childData = dayByString.toFloat()
            initBarCHart(totalCntGraph ,childData)
        })
    }


    private fun initBarCHart(barChart: BarChart, childCnt: Float) {

        Log.d("childCnt = ", childCnt.toString())
        myChildTotalCnt = childCnt

        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1f,myChildTotalCnt))
        entries.add(BarEntry(2f,fatTotalCnt))
        barChart.run {
            description.isEnabled = false
            setMaxVisibleValueCount(10)
            setPinchZoom(false)
            setDrawBarShadow(false)
            setDrawGridBackground(false)
            axisLeft.run {
                axisMaximum = if(myChildTotalCnt > fatTotalCnt){
                    myChildTotalCnt + 1f
                } else{
                    fatTotalCnt + 1f
                }

                axisMinimum = 0f
                val ll = LimitLine(myChildTotalCnt )
                ll.lineWidth = 3f
                ll.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
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
        feedback1.value = feedbackText
    }

    fun updateGraph(touchedDay: LocalDate){
        //val childData = getDataforDay(selectedDay)
        val childData = 100f
        initBarCHart(totalCntGraph ,childData)
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

    fun getChildGenderFromPref(): String {
        val pref: SharedPreferences = requireContext().getSharedPreferences("selectedChildGender", Context.MODE_PRIVATE)
        val gender = pref.getString("selectedChildGender", "없음").toString()

        return gender
    }

}