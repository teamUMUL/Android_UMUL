import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import inu.thebite.umul.fragment.bottomNavFragment.ReportFragment
import inu.thebite.umul.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CalendarAdapter(private val context: ReportFragment,
                      private val data: ArrayList<Date>,
                      private val currentDate: Calendar,
                      private val changeMonth: Calendar?): RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    private var mListener: OnItemClickListener? = null
    private var index = -1

    private var selectCurrentDate = true
    private val currentMonth = currentDate[Calendar.MONTH]
    private val currentYear = currentDate[Calendar.YEAR]
    private val currentDay = currentDate[Calendar.DAY_OF_MONTH]
    private val selectedDay =
        when {
            changeMonth != null -> changeMonth.getActualMinimum(Calendar.DAY_OF_MONTH)
            else -> currentDay
        }
    private val selectedMonth =
        when {
            changeMonth != null -> changeMonth[Calendar.MONTH]
            else -> currentMonth
        }
    private val selectedYear =
        when {
            changeMonth != null -> changeMonth[Calendar.YEAR]
            else -> currentYear
        }



    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_list, parent, false), mListener!!)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val sdf = SimpleDateFormat("E", Locale.KOREAN)
        val cal = Calendar.getInstance(Locale.KOREAN)
        cal.time = data[position]

        val displayMonth = cal[Calendar.MONTH]
        val displayYear= cal[Calendar.YEAR]
        val displayDay = cal[Calendar.DAY_OF_MONTH]

        try {
            holder.txtDayInWeek!!.text = sdf.format(cal.time)
        } catch (ex: ParseException) {
            Log.v("Exception", ex.localizedMessage!!)
        }
        holder.txtDay!!.text = cal[Calendar.DAY_OF_MONTH].toString()




        holder.linearLayout!!.setOnClickListener {
            index = position
            selectCurrentDate = false
            holder.listener.onItemClick(position)
            notifyDataSetChanged()
        }

        if (index == position)
            makeItemSelected(holder)
        else {
            if (displayDay == selectedDay
                && displayMonth == selectedMonth
                && displayYear == selectedYear
                && selectCurrentDate)
                makeItemSelected(holder)
            else
                makeItemDefault(holder)
        }




    }

    inner class ViewHolder(itemView: View, val listener: OnItemClickListener): RecyclerView.ViewHolder(itemView) {
        var txtDay = itemView.findViewById<TextView>(R.id.txt_date)
        var txtDayInWeek = itemView.findViewById<TextView>(R.id.txt_day)
        var linearLayout = itemView.findViewById<LinearLayout>(R.id.calendar_linear_layout)
    }

    //달력 item별 간격 설정




    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }



    private fun makeItemSelected(holder: ViewHolder) {
        holder.linearLayout!!.setBackgroundResource(R.drawable.calendar_selected)
    }

    private fun makeItemDefault(holder: ViewHolder) {
        holder.txtDay!!.setTextColor(Color.BLACK)
        holder.txtDayInWeek!!.setTextColor(Color.BLACK)
        holder.linearLayout!!.setBackgroundColor(Color.WHITE)
    }
}


