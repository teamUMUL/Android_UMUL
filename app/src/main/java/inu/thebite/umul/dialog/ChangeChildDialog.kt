package inu.thebite.umul.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import inu.thebite.umul.R
import inu.thebite.umul.activity.MainActivity
import inu.thebite.umul.adapter.ChangeChildAdapter
import inu.thebite.umul.model.SaveChildrenResponse
import inu.thebite.umul.retrofit.RetrofitAPI
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception

class ChangeChildDialog : DialogFragment(), View.OnClickListener {
    private lateinit var viewGroup: ViewGroup
    lateinit var calendarList: RecyclerView
    lateinit var mLayoutManager: LinearLayoutManager
    val childKey: MutableList<String> = mutableListOf()
    val childValue: MutableList<String> = mutableListOf()
    private lateinit var memberNumber: String
    var tempDateList: MutableList<String> = mutableListOf()   // 이 mi친 비동기
    val bundle = Bundle()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewGroup = inflater.inflate(R.layout.change_child_dialog, container, false) as ViewGroup
        val memberNumberPref = requireContext().getSharedPreferences("MemberNumber", Context.MODE_PRIVATE)
        memberNumber =  memberNumberPref.getString("MemberNumber", "010-0000-0000").toString()
        Log.d("ChangeChildDialog memberNumber = ", memberNumber)
        getChildrenList(memberNumber)


        calendarList = viewGroup.findViewById(R.id.child_select_recyclerView)
        calendarList.setHasFixedSize(true)
        calendarList.isNestedScrollingEnabled = false
        mLayoutManager = LinearLayoutManager(viewGroup.context)
        calendarList.setHasFixedSize(false)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        calendarList.layoutManager = mLayoutManager
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(calendarList)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))



        return viewGroup
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListener()

    }

    private fun setOnClickListener() {
        val closeBtn: ImageButton = viewGroup.findViewById(R.id.child_select_cancel)
        val addChildBtn: ImageButton = viewGroup.findViewById(R.id.child_add_button)


        closeBtn.setOnClickListener(this)
        addChildBtn.setOnClickListener(this)

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.child_select_cancel -> {
                dialog?.dismiss()
            }

            R.id.child_add_button -> {
                (activity as MainActivity?)?.startInsertInfoActivity()
            }
        }
    }


    @SuppressLint("CutPasteId")
    private fun setUpChangeChildDialog() {

        Log.d("childValue1 = ", childValue.toString())
        val pref: SharedPreferences = requireContext().getSharedPreferences("selectedChild", Context.MODE_PRIVATE)
        val selectedChildName = pref.getString("selectedChild", "홍길동").toString()

        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        viewGroup.findViewById<RecyclerView>(R.id.child_select_recyclerView)!!.layoutManager =
            layoutManager
        //처음 adapter연결할 때 SharedPreference에서 선택했던 값이 있는 경우에는 그 값을 없는 경우에는 자녀1을 기본으로 설정해서 adapter생성
        val changeChildAdapter =
            ChangeChildAdapter(childValue, selectedChildName, requireContext())
        viewGroup.findViewById<RecyclerView>(R.id.child_select_recyclerView)!!.adapter =
            changeChildAdapter


        changeChildAdapter.setOnItemClickListener(object : ChangeChildAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val selectedChildID = childValue[position]
                val pref: SharedPreferences = context!!.getSharedPreferences("selectedChild", Context.MODE_PRIVATE)
                val editor = pref.edit()
                //선택될 시 SharedPreference에 key-value로 선택한 id(ex: 자녀1, 자녀1 ...)가 저장
                editor.putString("selectedChild", selectedChildID)
                editor.apply()
            }
        })
    }

    private fun getChildrenList(memberNumber: String) {
        Thread {
            RetrofitAPI.emgMedService.getChildrenList(memberNumber)
                .enqueue(object : retrofit2.Callback<List<SaveChildrenResponse>> {
                    override fun onResponse(
                        call: Call<List<SaveChildrenResponse>>,
                        response: Response<List<SaveChildrenResponse>>
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            Log.d("자녀정보 리스트 가져오기 성공", "$result")

                            tempDateList.clear()

                            for (i: Int in 0 until result!!.size) {
                                tempDateList.add(result[i].name)
                            }
                            setChildrenList(tempDateList)
                        }
                    }

                    override fun onFailure(call: Call<List<SaveChildrenResponse>>, t: Throwable) {
                        Log.d("자녀정보 리스트 가져오기 실패", t.message.toString())
                    }
                })
        }.start()


        try {
            Thread.sleep(900)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setChildrenList(childrenValue: MutableList<String>) {
        childValue.clear()
        for (child in childrenValue) {
            childValue.add(child)
            Log.d("childValue2 = ", child)
        }

        setUpChangeChildDialog()
    }
}