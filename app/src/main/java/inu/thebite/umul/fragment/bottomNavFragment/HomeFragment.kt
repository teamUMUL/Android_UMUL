package inu.thebite.umul.fragment.bottomNavFragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
    private lateinit var memberNumber: String
    private lateinit var childName: String
    val bundle = Bundle()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.homeFragment = this
        binding.lifecycleOwner = this
        memberNumber = getMemberNumberFromPref()
        childName = getChildNameFromPref()
        Log.d("HomeFragment memberNumber = ", memberNumber)
        Log.d("HomeFragment childName = ", childName)

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
                bundle.putString("memberNumber", memberNumber)
                bundle.putString("childName", childName)
                val recordReadyFragment = RecordReadyFragment()
                recordReadyFragment.arguments = bundle
                parentFragmentManager.beginTransaction().replace(R.id.mainFrame, recordReadyFragment)
                    .commit()
                //가운데 플레이버튼 체크
                (activity as MainActivity?)?.setRecordChecked()
            }
            R.id.home_bmi_button -> {
                parentFragmentManager.beginTransaction().replace(R.id.mainFrame, BMIFragment())
                    .commit()
                //하단 바 BMI 체크
                (activity as MainActivity?)?.setBMIChecked()
            }
            R.id.shopBtn -> {
                //노션 페이지 열기
                setNotionUrl()
            }
            R.id.logo_home->{
                parentFragmentManager.beginTransaction().replace(R.id.mainFrame, HomeFragment())
                    .commit()
                (activity as MainActivity?)?.setHomeChecked()
            }
            R.id.home_ble_button -> {
                (activity as MainActivity?)?.setBLE()

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
        bundle.putString("memberNumber", memberNumber)
        val childDialog = ChangeChildDialog()
        childDialog!!.arguments = bundle
        childDialog.show(childFragmentManager, "ChangeChildDialog")
    }

    fun getMemberNumberFromPref(): String {
        val pref: SharedPreferences = requireContext().getSharedPreferences("MemberNumber", Context.MODE_PRIVATE)
        val memberNumber = pref.getString("MemberNumber", "010-0000-0000").toString()

        return memberNumber
    }
    fun getChildNameFromPref(): String {
        val pref: SharedPreferences = requireContext().getSharedPreferences("selectedChild", Context.MODE_PRIVATE)
        val childName = pref.getString("selectedChild", "홍길동").toString()

        return childName
    }
}