package inu.thebite.umul.fragment.bottomNavFragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Transformations.map
import inu.thebite.umul.R
import inu.thebite.umul.activity.MainActivity
import inu.thebite.umul.databinding.FragmentHomeBinding
import inu.thebite.umul.dialog.ChangeChildDialog
import inu.thebite.umul.model.SaveChildrenResponse
import inu.thebite.umul.retrofit.RetrofitAPI
import retrofit2.Call
import retrofit2.Response


class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentHomeBinding

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
    }

    private fun setOnClickListener() {
        val recordButton: ImageButton = binding.homeRecordButton
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


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.home_record_button -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.mainFrame, RecordReadyFragment())
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

            R.id.logo_home -> {
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

    fun setNotionUrl() {
        val browserIntent = Intent(
            Intent.ACTION_VIEW, Uri.parse("https://bit.ly/aboutthebite")
        )
        requireContext().startActivity(browserIntent);
    }

    private fun showChangeChildDialog() {
        val childDialog = ChangeChildDialog()
        val args = Bundle()
        for (key in data.keys) {
            args.putString(key, data[key])
        }
        childDialog.arguments = args
        childDialog.show(childFragmentManager, "ChangeChildDialog")
    }


}