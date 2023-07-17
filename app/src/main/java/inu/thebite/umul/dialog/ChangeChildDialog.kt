package inu.thebite.umul.dialog

import android.annotation.SuppressLint
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
    var memberNumber = "010-1234-5678"
    var selectedChildID: String? = null
    var tempDateList: MutableList<String> = mutableListOf()    // 이 mi친 비동기


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewGroup = inflater.inflate(R.layout.change_child_dialog, container, false) as ViewGroup

        val pref = requireActivity().getPreferences(0)
        val editor = pref.edit()
        selectedChildID = pref.getString("selecetedChild", "자녀1")

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

        setUpChangeChildDialog()

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
        val pref = requireActivity().getPreferences(0)

        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        getChildrenList(memberNumber)

        viewGroup.findViewById<RecyclerView>(R.id.child_select_recyclerView)!!.layoutManager =
            layoutManager
        //처음 adapter연결할 때 SharedPreference에서 선택했던 값이 있는 경우에는 그 값을 없는 경우에는 자녀1을 기본으로 설정해서 adapter생성
        val changeChildAdapter =
            ChangeChildAdapter(childKey, childValue, pref.getString("selectedChild", "자녀1"))
        viewGroup.findViewById<RecyclerView>(R.id.child_select_recyclerView)!!.adapter =
            changeChildAdapter


        changeChildAdapter.setOnItemClickListener(object : ChangeChildAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                selectedChildID = childValue[position]
                val pref = requireActivity().getPreferences(0)
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
            Thread.sleep(300)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setChildrenList(childrenValue: MutableList<String>) {
        childValue.clear()
        for (child in childrenValue) {
            childValue.add(child)
        }
    }

}