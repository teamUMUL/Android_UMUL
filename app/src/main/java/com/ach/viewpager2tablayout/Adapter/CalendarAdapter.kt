import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ach.viewpager2tablayout.CalendarVO.CalendarVO
import com.ach.viewpager2tablayout.R
import com.ach.viewpager2tablayout.databinding.ItemCalendarListBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class CalendarAdapter(private val cList: List<CalendarVO>) :
    RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    var drawable: Drawable? = null

    private lateinit var itemClickListener: AdapterView.OnItemClickListener

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTv: TextView = view.findViewById(R.id.date)
        val dayTv: TextView = view.findViewById(R.id.day)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_calendar_list, viewGroup, false)

        drawable = ContextCompat.getDrawable(view.context, R.drawable.calendar_selected)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dateTv.text = cList[position].cl_date
        holder.dayTv.text = cList[position].cl_day
    }

    override fun getItemCount() = cList.size
}







/*    class CalendarViewHolder(private val binding: ItemCalendarListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CalendarVO) {
            binding.date.text = item.cl_date
            binding.day.text = item.cl_day

            var today = binding.date.text

            // 오늘 날짜
            val now = LocalDate.now().format(DateTimeFormatter.ofPattern("dd").withLocale(Locale.forLanguageTag("ko")))
            // 오늘 날짜와 캘린더의 오늘 날짜가 같을 경우 background 적용하기

            if (today == now) {
                binding.weekCardview.setBackgroundResource(R.drawable.calendar_selected)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ItemCalendarListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(cList[position])
    }

    override fun getItemCount(): Int {
        return cList.size
    }*/


