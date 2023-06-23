package inu.thebite.umul.fragment.bottomNavFragment

import CalendarAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import inu.thebite.umul.adapter.ReportPagerAdapter
import inu.thebite.umul.adapter.decoration.CalendarAdapterDecoration
import inu.thebite.umul.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class ReportFragment : Fragment() {
    private lateinit var viewGroup: ViewGroup

    lateinit var calendarList: RecyclerView
    lateinit var mLayoutManager: LinearLayoutManager

    //날짜 선택 및 범위 설정
    private val lastDayInCalendar = Calendar.getInstance(Locale.KOREAN)
    private val sdf = SimpleDateFormat("yyyy MMMM", Locale.KOREAN)
    private val cal = Calendar.getInstance(Locale.KOREAN)

    private val currentDate = Calendar.getInstance(Locale.KOREAN)
    private val currentDay = currentDate[Calendar.DAY_OF_MONTH]
    private val currentMonth = currentDate[Calendar.MONTH] + 1
    private val currentYear = currentDate[Calendar.YEAR]

    private var selectedDay: Int = currentDay
    private var selectedMonth: Int = currentMonth
    private var selectedYear: Int = currentYear

    private val dates = ArrayList<Date>()

    private lateinit var outputDay : String
    private lateinit var outputMonth : String
    private lateinit var outputDate : LocalDate
    //달력 설정

   //레포트
    private val tabLists = listOf(
        "일간 레포트",
        "주간 레포트"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewGroup = inflater.inflate(R.layout.fragment_report, container, false) as ViewGroup
        val viewPager: ViewPager2 = viewGroup.findViewById<ViewPager2>(R.id.viewPager)
        val tabs: TabLayout = viewGroup.findViewById<TabLayout>(R.id.tabs)

        calendarList = viewGroup.findViewById(R.id.calendar_recycler_view)
        mLayoutManager = LinearLayoutManager(viewGroup.context)


        //달력 가로 방향 스크롤 설정
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        calendarList.layoutManager = mLayoutManager

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(calendarList)

        lastDayInCalendar.add(Calendar.MONTH, 6)

        //상단 달력
        setUpCalendar()

        viewGroup.findViewById<ImageButton>(R.id.calendar_prev_button)!!.setOnClickListener {

            cal.add(Calendar.MONTH, -1)
            if (cal == currentDate)
                setUpCalendar()
            else
                setUpCalendar(changeMonth = cal)

        }


        viewGroup.findViewById<ImageButton>(R.id.calendar_next_button)!!.setOnClickListener {
            cal.add(Calendar.MONTH, +1)
            if (cal == currentDate)
                setUpCalendar()
            else
                setUpCalendar(changeMonth = cal)
        }



        val itemDecoration = CalendarAdapterDecoration(10)
        calendarList.addItemDecoration(itemDecoration)



        //스와이프뷰
        setInit()

        TabLayoutMediator(tabs, viewPager){
                tab, position ->
            tab.text = tabLists[position]
        }.attach()

        return viewGroup
    }


    @SuppressLint("CutPasteId")
    private fun setUpCalendar(changeMonth: Calendar? = null) {
        viewGroup.findViewById<TextView>(R.id.txt_current_month)!!.text = sdf.format(cal.time)
        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        selectedDay =
            when {
                changeMonth != null -> changeMonth.getActualMinimum(Calendar.DAY_OF_MONTH)
                else -> currentDay
            }
        selectedMonth =
            when {
                changeMonth != null -> changeMonth[Calendar.MONTH]+1
                else -> currentMonth
            }
        selectedYear =
            when {
                changeMonth != null -> changeMonth[Calendar.YEAR]
                else -> currentYear
            }

        var currentPosition = 0
        dates.clear()
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)

        while (dates.size < maxDaysInMonth) {
            // get position of selected day
            if (monthCalendar[Calendar.DAY_OF_MONTH] == selectedDay)
                currentPosition = dates.size
            dates.add(monthCalendar.time)
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Assigning calendar view.
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        viewGroup.findViewById<RecyclerView>(R.id.calendar_recycler_view)!!.layoutManager = layoutManager
        val calendarAdapter = CalendarAdapter(this, dates, currentDate, changeMonth)
        viewGroup.findViewById<RecyclerView>(R.id.calendar_recycler_view)!!.adapter = calendarAdapter

        when {
            currentPosition > 2 -> viewGroup.findViewById<RecyclerView>(R.id.calendar_recycler_view)!!.scrollToPosition(currentPosition - 3)
            maxDaysInMonth - currentPosition < 2 -> viewGroup.findViewById<RecyclerView>(R.id.calendar_recycler_view)!!.scrollToPosition(currentPosition)
            else -> viewGroup.findViewById<RecyclerView>(R.id.calendar_recycler_view)!!.scrollToPosition(currentPosition)
        }


        calendarAdapter.setOnItemClickListener(object : CalendarAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val clickCalendar = Calendar.getInstance()
                clickCalendar.time = dates[position]
                selectedDay = clickCalendar[Calendar.DAY_OF_MONTH]

                //한자리 날짜인 경우 앞에 0붙여주기
                outputDay = if(selectedDay.toString().length==1){
                    "0$selectedDay"
                } else{
                    selectedDay.toString()
                }
                //한자리 개월인 경우 앞에 0붙여주기
                outputMonth = if(selectedMonth.toString().length==1){
                    "0$selectedMonth"
                } else{
                    selectedMonth.toString()
                }
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                outputDate = LocalDate.parse("$selectedYear-$outputMonth-$outputDay", formatter)
                println(outputDate)
            }
        })
    }







    private fun setInit(){

        val viewPageSetUp : ViewPager2 = viewGroup.findViewById(R.id.viewPager)
        val setUpPagerAdapter =
            activity.let{ ReportPagerAdapter(it!!) }

        viewPageSetUp.adapter = setUpPagerAdapter
        viewPageSetUp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPageSetUp.offscreenPageLimit = 2

    }
}


