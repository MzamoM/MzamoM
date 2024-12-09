package com.sos.msgroup.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.sos.msgroup.databinding.FragmentAboutUsBinding


class AboutUsFragment : Fragment() {

    private var _binding: FragmentAboutUsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var imageViewWebsite: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentAboutUsBinding.inflate(inflater, container, false)

        imageViewWebsite = binding.imgWebsite

        imageViewWebsite.setOnClickListener {
            val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.msgroupsa.co.za/"))
            startActivity(myIntent)
        }

        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}