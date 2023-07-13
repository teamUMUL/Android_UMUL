package inu.thebite.umul.adapter

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import inu.thebite.umul.R
import inu.thebite.umul.dialog.ChangeChildDialog
import java.util.Calendar

class ChangeChildAdapter(var childKey: MutableList<String>, var childValue : MutableList<String>, selectedChild : String?) : RecyclerView.Adapter<ChangeChildAdapter.ViewHolder>() {
    private var mListener: OnItemClickListener? = null
    private var index = -1
    private var selectFirstChild = true
    private val selectedChildID =
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

        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.change_child_item, parent, false), mListener!!)
    }

    override fun getItemCount(): Int {
        return childKey.size
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ChangeChildAdapter.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val childNum = childKey[position]
        val childInfo = childValue[position]

        val displayChildID = "자녀"+(position+1).toString()
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
            Toast.makeText(holder.itemView.context, "$childNum : $childInfo",Toast.LENGTH_SHORT).show();
        }
        else{
            if (displayChildID == selectedChildID
                && selectFirstChild) {
                Toast.makeText(holder.itemView.context, displayChildID.toString(), Toast.LENGTH_SHORT).show()
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