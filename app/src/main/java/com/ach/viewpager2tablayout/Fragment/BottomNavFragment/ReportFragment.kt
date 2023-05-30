package com.ach.viewpager2tablayout.Fragment.BottomNavFragment

import CalendarAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ach.viewpager2tablayout.Adapter.ReportPagerAdapter
import com.ach.viewpager2tablayout.CalendarVO.CalendarVO
import com.ach.viewpager2tablayout.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.*
import kotlin.collections.ArrayList

class ReportFragment : Fragment() {
    private lateinit var viewGroup: ViewGroup
    //private var _binding : FragmentReportBinding? = null
    //private val binding get() = _binding!!

    private val itemList = arrayListOf<CalendarVO>()
    private val listAdapter = CalendarAdapter(itemList)
    lateinit var calendarList: RecyclerView
    lateinit var mLayoutManager: LinearLayoutManager



    //lateinit var calendarAdapter: CalendarAdapter

/*    companion object{
        fun newInstance() = ReportFragment()
    }*/

    //레포트
    private val tabLists = listOf(
        "일간 레포트",
        "주간 레포트"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //_binding = FragmentReportBinding.inflate(inflater, container, false)
        viewGroup = inflater.inflate(R.layout.fragment_report, container, false) as ViewGroup
        val viewPager: ViewPager2 = viewGroup.findViewById<ViewPager2>(R.id.viewPager)
        val tabs: TabLayout = viewGroup.findViewById<TabLayout>(R.id.tabs)

        calendarList = viewGroup.findViewById(R.id.calendarView)
        mLayoutManager = LinearLayoutManager(viewGroup.context)


        //달력 가로 방향 스크롤 설정
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        calendarList.layoutManager = mLayoutManager


        //상단 달력
        setCalendarListView()
        //스와이프뷰
        setInit()







        TabLayoutMediator(tabs, viewPager){
                tab, position ->
            tab.text = tabLists[position]
        }.attach()

        return viewGroup
    }

    // list(날짜, 요일)를 만들고, adapter를 등록하는 메소드
    private fun setCalendarListView() {
        // 현재 달의 마지막 날짜
        val lastDayOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
        var lastDay = lastDayOfMonth.format(DateTimeFormatter.ofPattern("dd")).toInt()
        for(i: Int in 1..lastDay) {
            val date = LocalDate.of(LocalDate.now().year, LocalDate.now().month, i)
            val dayOfWeek: DayOfWeek = date.dayOfWeek


            itemList.add(CalendarVO(dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN).toString(), i.toString()))
        }
        calendarList.adapter = listAdapter
    }



/*    private fun setListView() {
        // 현재 달의 마지막 날짜
        val lastDayOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth())
        lastDayOfMonth.format(DateTimeFormatter.ofPattern("dd"))

        for(i: Int in 1..lastDayOfMonth) {
            val date = LocalDate.of(LocalDate.now().year, LocalDate.now().month, i)
            val dayOfWeek: DayOfWeek = date.dayOfWeek
            dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)

            itemList.add(Date(dayOfWeek.toString().substring(0, 3), i.toString()))
        }
        calendarList.adapter = listAdapter
    }*/





/*    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var week_day: Array<String> = resources.getStringArray(R.array.calendar_day)

        calendarAdapter = CalendarAdapter(calendarList)

        calendarList.apply {
            val dateFormat = DateTimeFormatter.ofPattern("dd").withLocale(Locale.forLanguageTag("ko"))
            val monthFormat = DateTimeFormatter.ofPattern("MM월").withLocale(Locale.forLanguageTag("ko"))

            val localDate = LocalDateTime.now().format(monthFormat)
            binding.calendarMonth.text = localDate

            var preSunday: LocalDateTime = LocalDateTime.now().with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))

            for (i in 0..6) {
                Log.d("날짜만", week_day[i])

                calendarList.apply {
                    add(CalendarVO(preSunday.plusDays(i.toLong()).format(dateFormat), week_day[i]))
                }
                Log.d("저번 주 일요일 기준으로 시작!", preSunday.plusDays(i.toLong()).format(dateFormat))
            }
        }
        binding.calendarView.adapter = calendarAdapter
        binding.calendarView.layoutManager = GridLayoutManager(context, 7)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }*/




    private fun setInit(){

        val viewPageSetUp : ViewPager2 = viewGroup.findViewById(R.id.viewPager)
        val setUpPagerAdapter =
            activity.let{ ReportPagerAdapter(it!!) }

        viewPageSetUp.adapter = setUpPagerAdapter
        viewPageSetUp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPageSetUp.offscreenPageLimit = 2

    }
}


