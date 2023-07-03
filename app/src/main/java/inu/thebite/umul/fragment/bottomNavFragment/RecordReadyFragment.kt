package inu.thebite.umul.fragment.bottomNavFragment

import android.content.Intent
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
import inu.thebite.umul.activity.RecordActivity
import inu.thebite.umul.databinding.FragmentRecordReadyBinding

class RecordReadyFragment : Fragment(), View.OnClickListener {

    private lateinit var binding : FragmentRecordReadyBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_record_ready, container, false)
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

        gameStartBtn.setOnClickListener(this)
        shopBtn.setOnClickListener(this)
        logoBtn.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            //게임화면
            R.id.gameStart -> {
                (activity as MainActivity?)?.setGameActivityStart()
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

    fun setNotionUrl(){
        val browserIntent = Intent(
            Intent.ACTION_VIEW, Uri.parse("https://bit.ly/aboutthebite")
        )
        requireContext().startActivity(browserIntent);
    }
}