package com.technic.blueshark.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.technic.blueshark.R
import com.technic.blueshark.databinding.FragmentScanBinding
import com.technic.blueshark.utils.hasRequiredRuntimePermissions
import com.technic.blueshark.utils.requestRelevantRuntimePermissions

class ScanFragment : BaseFragment<FragmentScanBinding>(FragmentScanBinding::inflate) {

    private lateinit var scanBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.let {
            scanBtn = it.btnScan
        }

        scanBtn.setOnClickListener {
            startBleScan()
        }

    }

    private fun startBleScan() {
        with (mContext) {
            if (!hasRequiredRuntimePermissions()) {
                requireActivity().requestRelevantRuntimePermissions()
            } else {
                //ToDo: actually perform the scan
            }
        }
    }

    companion object {
    }
}