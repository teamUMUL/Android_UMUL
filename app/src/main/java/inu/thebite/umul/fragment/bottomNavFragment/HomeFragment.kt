package inu.thebite.umul.fragment.bottomNavFragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import inu.thebite.umul.R
import inu.thebite.umul.activity.MainActivity
import inu.thebite.umul.databinding.FragmentHomeBinding
import inu.thebite.umul.dialog.ChangeChildDialog


@Suppress("DEPRECATION")
class HomeFragment : Fragment(), View.OnClickListener {
    private lateinit var binding : FragmentHomeBinding
    private lateinit var mainActivity : MainActivity
    private var isBluetoothConnected = false
    var data = mapOf<String, String>(
        "자녀1" to "홍길동(8세)","자녀2" to "홍길동(4세)","자녀3" to "홍길동(6세)","자녀4" to "홍길동(3세)","자녀5" to "홍길동(7세)",
        "자녀6" to "홍길동(8세)","자녀7" to "홍길동(4세)","자녀8" to "홍길동(6세)","자녀9" to "홍길동(3세)","자녀10" to "홍길동(7세)"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.homeFragment = this
        binding.lifecycleOwner = this

        return binding.root
    }

    //MainActivity에서 저장했던 연결 유무 값을 얻어서 연결 유무에 따른 이미지 설정
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBluetoothConnectionInfo()
        checkBluetoothConnected(isBluetoothConnected)

        setOnClickListener()
    }

    private fun setOnClickListener(){
        val recordButton = binding.homeRecordButton
        val bmiButton = binding.homeBmiButton
        val bleButton = binding.homeBleButton
        val shopBtn = binding.shopBtn
        val logoBtn = binding.logoHome
        val child = binding.child

        recordButton.setOnClickListener(this)
        bmiButton.setOnClickListener(this)
        bleButton.setOnClickListener(this)
        shopBtn.setOnClickListener(this)
        logoBtn.setOnClickListener(this)
        child.setOnClickListener(this)
    }

    override fun onClick(v: View?){
        when(v?.id){
            R.id.home_record_button -> {
                parentFragmentManager.beginTransaction().replace(R.id.mainFrame, RecordReadyFragment())
                    .commit()
                //가운데 플레이버튼 체크
                mainActivity.setRecordChecked()
            }
            R.id.home_bmi_button -> {
                parentFragmentManager.beginTransaction().replace(R.id.mainFrame, BMIFragment())
                    .commit()
                //하단 바 BMI 체크
                mainActivity.setBMIChecked()
            }
            R.id.shopBtn -> {
                //노션 페이지 열기
                setNotionUrl()
            }
            R.id.logo_home->{
                parentFragmentManager.beginTransaction().replace(R.id.mainFrame, HomeFragment())
                    .commit()
                mainActivity.setHomeChecked()
            }
            R.id.home_ble_button -> {
                mainActivity.setBLE()

            }
            R.id.child -> {
                showChangeChildDialog()
            }

        }
    }

    private fun setNotionUrl(){
        val browserIntent = Intent(
            Intent.ACTION_VIEW, Uri.parse("https://bit.ly/aboutthebite")
        )
        requireContext().startActivity(browserIntent);
    }

    //sharedPreference에서 블루투스 연결 유무 확인 정보 얻음
    private fun getBluetoothConnectionInfo(){
        val pref: SharedPreferences = requireActivity().getSharedPreferences("BluetoothConnection", Context.MODE_PRIVATE)
        isBluetoothConnected = pref.getBoolean("isBluetoothConnected", false)
    }

    private fun checkBluetoothConnected(isConnected : Boolean){
        //블루투스 연결 유무에 따른 이미지 변경
        if(isConnected){
            binding.homeBleButton.setImageResource(R.drawable.bluetooth_connected)
        }
        else{
            binding.homeBleButton.setImageResource(R.drawable.bluetooth_disconnected)
        }
    }

    private fun showChangeChildDialog(){
        //자녀 리스트를 argument로 ChangeChildDialog로 전달
        val childDialog = ChangeChildDialog()
        val args = Bundle()
        for (key in data.keys) {
            args.putString(key, data[key])
        }
        childDialog.arguments = args
        childDialog.show(childFragmentManager, "ChangeChildDialog")
    }

}