package inu.thebite.umul.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import inu.thebite.umul.R


class ChangeChildAdapter(var childValue : MutableList<String>, selectedChild : String?, private val context: Context) : RecyclerView.Adapter<ChangeChildAdapter.ViewHolder>() {
    private var mListener: OnItemClickListener? = null
    private var index = -1
    private var selectFirstChild = true
    private var selectedChildID =
        when {
            selectedChild != null -> selectedChild
            else -> "자녀1"
        }
    inner class ViewHolder(itemView: View, val listener: OnItemClickListener): RecyclerView.ViewHolder(itemView) {
        //var childNum = itemView.findViewById<TextView>(R.id.child_num)
        var childInfo = itemView.findViewById<TextView>(R.id.child_info)
        var frameLayout = itemView.findViewById<FrameLayout>(R.id.child_layout)
    }


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        Log.d("childValue in adapter = ", childValue.toString())

        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.change_child_item, parent, false), mListener!!)
    }

    override fun getItemCount(): Int {
        return childValue.size
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ChangeChildAdapter.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
//      val childNum = childKey[position]
        val childInfo = childValue[position]
        val pref: SharedPreferences = context.getSharedPreferences("selectedChild", Context.MODE_PRIVATE)
        val editor = pref.edit()
        selectedChildID = pref.getString("selectedChild", "ㅇㅇㅇ").toString()
        //holder.childNum.text = childNum
        holder.childInfo.text = childInfo

        holder.frameLayout!!.setOnClickListener {
            index = position
            selectFirstChild = false
            holder.listener.onItemClick(position)
            notifyDataSetChanged()
        }

        if (index == position){
            makeItemSelected(holder)
            Toast.makeText(holder.itemView.context, "$selectedChildID",Toast.LENGTH_SHORT).show();
        }
        else{
            if (childInfo == selectedChildID
                && selectFirstChild) {
                makeItemSelected(holder)
            }
            else {
                makeItemDefault(holder)
            }
        }

    }



    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    private fun makeItemSelected(holder: ViewHolder) {
        holder.frameLayout!!.setBackgroundResource(R.drawable.child_dialog_selected_border)

    }
    private fun makeItemDefault(holder: ViewHolder) {
        holder.frameLayout!!.setBackgroundResource(R.drawable.child_dialog_border)
    }


}