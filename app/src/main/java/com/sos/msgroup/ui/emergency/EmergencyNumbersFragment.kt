package com.sos.msgroup.ui.emergency

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sos.msgroup.databinding.FragmentEmergencyNumbersBinding


class EmergencyNumbersFragment : Fragment() {
    private var _binding: FragmentEmergencyNumbersBinding? = null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentEmergencyNumbersBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

}