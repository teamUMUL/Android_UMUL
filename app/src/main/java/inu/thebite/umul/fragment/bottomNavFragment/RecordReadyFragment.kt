package inu.thebite.umul.fragment.bottomNavFragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import inu.thebite.umul.R
import inu.thebite.umul.activity.MainActivity
import inu.thebite.umul.databinding.CustomToastBinding
import inu.thebite.umul.databinding.FragmentRecordReadyBinding


@Suppress("DEPRECATION")
class RecordReadyFragment : Fragment(), View.OnClickListener {

    private lateinit var binding : FragmentRecordReadyBinding
    private lateinit var customToastBinding : CustomToastBinding
    private var isBluetoothConnected : Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_record_ready, container, false)
        binding.recordReadyFragment = this
        binding.lifecycleOwner = this
        getBluetoothConnectionInfo()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
    }

    private fun setOnClickListener(){
        val gameStartBtn : ImageButton = binding.gameStart
        val shopBtn : ImageButton = binding.shopBtn
        val logoBtn : ImageButton = binding.logoHome

        gameStartBtn.setOnClickListener(this)
        shopBtn.setOnClickListener(this)
        logoBtn.setOnClickListener(this)

    }

    @SuppressLint("ShowToast")
    override fun onClick(v: View?) {
        when(v?.id) {
            //게임화면
            R.id.gameStart -> {
                if(isBluetoothConnected){
                    (activity as MainActivity?)?.startRecordActivity()
                }
                else{
                    setCustomToast("Tinyam과 연결해주세요")

                }
            }

            R.id.shopBtn -> {
                setNotionUrl()
            }
            R.id.logo_home->{
                parentFragmentManager.beginTransaction().replace(R.id.mainFrame, HomeFragment())
                    .commit()
                (activity as MainActivity?)?.setHomeChecked()
            }
        }
    }
    //sharedPreference에서 블루투스 연결 유무 확인 정보 얻음
    private fun getBluetoothConnectionInfo(){
        val pref: SharedPreferences = requireActivity().getSharedPreferences("BluetoothConnection", Context.MODE_PRIVATE)
        isBluetoothConnected = pref.getBoolean("isBluetoothConnected", false)
    }

    fun setNotionUrl(){
        val browserIntent = Intent(
            Intent.ACTION_VIEW, Uri.parse("https://bit.ly/aboutthebite")
        )
        requireContext().startActivity(browserIntent);
    }

    fun setCustomToast(toastText : String){
        customToastBinding = CustomToastBinding.inflate(layoutInflater)
        val toastMessage : TextView = customToastBinding.toastMessage
        toastMessage.text = toastText

        val toast : Toast = Toast(activity)
        toast.duration = Toast.LENGTH_LONG
        toast.view = customToastBinding.root
        toast.show()
    }
}