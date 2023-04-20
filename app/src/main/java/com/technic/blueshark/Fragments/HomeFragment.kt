package com.technic.blueshark.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.technic.blueshark.R
import com.technic.blueshark.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private lateinit var navigateBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.let {
            navigateBtn = it.btnNavigate
        }

        navigateBtn.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToScanFragment(testArg = "from home")
            findNavController().navigate(action)
        }
    }

    companion object {

    }
}