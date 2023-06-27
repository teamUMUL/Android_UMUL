package inu.thebite.umul.fragment.bottomNavFragment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import inu.thebite.umul.R
import inu.thebite.umul.activity.MainActivity
import inu.thebite.umul.databinding.ActivityMainBinding
import inu.thebite.umul.databinding.FragmentHomeBinding


class HomeFragment : Fragment(), View.OnClickListener {

    val disabledButtonColor = Color.rgb(62, 97, 67) //버튼 비활성화 색 = Gray
    private val enabledButtonColor = Color.rgb(0,199,255) //버튼 활성화 색 = Aqua_Blue
    private lateinit var binding : FragmentHomeBinding
    private lateinit var activityBinding : ActivityMainBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        activityBinding = ActivityMainBinding.inflate(layoutInflater)
        binding.homeFragment = this
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
    }

    private fun setOnClickListener(){
        val recordButton : ImageButton  = binding.homeRecordButton
        val bmiButton = binding.homeBmiButton
        val bleButton = binding.homeBleButton
        val shopBtn = binding.shopBtn

        recordButton.setOnClickListener(this)
        bmiButton.setOnClickListener(this)
        bleButton.setOnClickListener(this)
        shopBtn.setOnClickListener(this)

    }


    override fun onClick(v: View?){
        when(v?.id){
            R.id.home_record_button -> {
                parentFragmentManager.beginTransaction().replace(R.id.mainFrame, RecordFragment())
                    .commit()
                (activity as MainActivity?)?.setRecordChecked()
            }
            R.id.home_bmi_button -> {
                parentFragmentManager.beginTransaction().replace(R.id.mainFrame, BMIFragment())
                    .commit()
                (activity as MainActivity?)?.setBMIChecked()
            }
            R.id.shopBtn -> {
                setNotionUrl()
            }
            R.id.home_ble_button -> {
                (activity as MainActivity?)?.setBLE()

            }

        }
    }

    fun setNotionUrl(){
        val browserIntent = Intent(
            Intent.ACTION_VIEW, Uri.parse("https://bit.ly/aboutthebite")
        )
        requireContext().startActivity(browserIntent);
    }
}