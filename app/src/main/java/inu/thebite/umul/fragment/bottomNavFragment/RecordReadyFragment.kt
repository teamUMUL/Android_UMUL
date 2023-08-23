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
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
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
    private lateinit var memberNumber: String
    private lateinit var childName: String
    private var gameState : String = "Carrot"
    private var gameLevelState : Int = 1
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_record_ready, container, false)

        memberNumber = arguments?.getString("memberNumber").toString()
        childName = arguments?.getString("childName").toString()
        binding.recordReadyFragment = this
        binding.lifecycleOwner = this

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
        val nextGameBtn : ImageButton = binding.nextGame
        val prevGameBtn : ImageButton = binding.prevGame
        val gameLevelHighBtn : RadioButton = binding.gameLevelHigh
        val gameLevelMiddleBtn : RadioButton = binding.gameLevelMiddle
        val gameLevelLowBtn : RadioButton = binding.gameLevelLow


        gameStartBtn.setOnClickListener(this)
        shopBtn.setOnClickListener(this)
        logoBtn.setOnClickListener(this)
        nextGameBtn.setOnClickListener(this)
        prevGameBtn.setOnClickListener(this)
        gameLevelHighBtn.setOnClickListener(this)
        gameLevelMiddleBtn.setOnClickListener(this)
        gameLevelLowBtn.setOnClickListener(this)
    }

    @SuppressLint("ShowToast")
    override fun onClick(v: View?) {
        val mainImage : ImageView = binding.mainImage

        when(v?.id) {
            //게임화면
            R.id.gameStart -> {
                getBluetoothConnectionInfo()
                if(isBluetoothConnected){
                    (activity as MainActivity?)?.startRecordActivity(gameState, gameLevelState)
                }
                else{
                    setCustomToast("Tinyam과 연결해주세요")
                }
                (activity as MainActivity?)?.startRecordActivity(gameState, gameLevelState)

            }

            R.id.shopBtn -> {
                setNotionUrl()
            }
            R.id.logo_home->{
                parentFragmentManager.beginTransaction().replace(R.id.mainFrame, HomeFragment())
                    .commit()
                (activity as MainActivity?)?.setHomeChecked()
            }
            R.id.prev_game->{
                if(gameState=="Carrot"){
                    gameState="Balloon"
                    mainImage.setImageResource(R.drawable.temp_image_balloon_game_ready)
                } else{
                    gameState="Carrot"
                    mainImage.setImageResource(R.drawable.temp_image_carrot_game_ready)
                }
            }
            R.id.next_game->{
                if(gameState=="Carrot"){
                    gameState="Balloon"
                    mainImage.setImageResource(R.drawable.temp_image_balloon_game_ready)
                } else{
                    gameState="Carrot"
                    mainImage.setImageResource(R.drawable.temp_image_carrot_game_ready)
                }
            }
            R.id.game_level_high->{
                gameLevelState = 5
            }
            R.id.game_level_middle->{
                gameLevelState = 3
            }
            R.id.game_level_low->{
                gameLevelState = 1
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