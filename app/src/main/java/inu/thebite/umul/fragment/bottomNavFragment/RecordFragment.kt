package inu.thebite.umul.fragment.bottomNavFragment

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import inu.thebite.umul.R
import inu.thebite.umul.activity.MainActivity
import inu.thebite.umul.databinding.FragmentRecordBinding
import inu.thebite.umul.dialog.GameEndDialog


class RecordFragment : Fragment(), View.OnClickListener {

    private lateinit var binding : FragmentRecordBinding
    var chewCount = 0
    var chewCountText = MutableLiveData("0")
    var spoonCount = 0
    var spoonCountText = MutableLiveData("0")
    var successCount = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_record, container, false)
        binding.recordFragment = this
        binding.lifecycleOwner = this
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
    }

    private fun setOnClickListener(){
        val chewBtn : Button = binding.chewBtn
        val spoonBtn : Button = binding.spoonBtn
        val endBtn : Button = binding.endBtn

        val shopBtn : ImageButton = binding.shopBtn
        val logoBtn : ImageButton = binding.logoHome

        chewBtn.setOnClickListener(this)
        spoonBtn.setOnClickListener(this)
        shopBtn.setOnClickListener(this)
        logoBtn.setOnClickListener(this)
        endBtn.setOnClickListener(this)

    }

    override fun onClick(v: View?){
        val imageView = binding.carrot
        when(v?.id) {
            //게임화면
            R.id.chewBtn -> {
                chewCount++
                chewCountText.value = chewCount.toString()
                if (chewCount >= 30 && chewCount % 2 == 0) {
                    imageView.setImageResource(R.drawable.carrot3)
                    val animatorX = ObjectAnimator.ofFloat(imageView, "translationX", 0f)
                    animatorX.duration = 300
                    animatorX.start()
                } else if (chewCount >= 30 && chewCount % 2 != 0) {
                    imageView.setImageResource(R.drawable.carrot4)
                    val animatorX = ObjectAnimator.ofFloat(imageView, "translationX", -3f)
                    animatorX.duration = 300
                    animatorX.start()
                } else if (chewCount < 30 && chewCount % 2 == 0) {
                    imageView.setImageResource(R.drawable.carrot1)
                    val animatorX = ObjectAnimator.ofFloat(imageView, "translationX", 0f)
                    animatorX.duration = 300
                    animatorX.start()
                } else {
                    imageView.setImageResource(R.drawable.carrot2)
                    val animatorX = ObjectAnimator.ofFloat(imageView, "translationX", -3f)
                    animatorX.duration = 300
                    animatorX.start()
                }

            }
            R.id.spoonBtn -> {
                spoonCount++
                spoonCountText.value = spoonCount.toString()
                if (chewCount >= 30){
                    successCount++
                }
                resetChewCount()
            }
            R.id.endBtn -> {
                resetChewCount()
                resetSpoonCount()
                showEndDialog()

                successCount = 0
            }

            //상단바
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

    fun resetChewCount(){
        chewCount = 0
        chewCountText.value = chewCount.toString()
    }

    fun resetSpoonCount(){
        spoonCount = 0
        spoonCountText.value = spoonCount.toString()
    }

    fun showEndDialog(){
        val dialog = GameEndDialog()
        val args = Bundle()
        args.putString("successNum" , successCount.toString())
        dialog.arguments = args
        dialog.show(childFragmentManager, "GameEndDialog")
    }
}