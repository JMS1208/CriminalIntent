package com.jms.a20220327_criminalintent.Fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jms.a20220327_criminalintent.R
import com.jms.a20220327_criminalintent.databinding.FragmentAddCrimeItemBinding


class AddCrimeItemFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAddCrimeItemBinding.inflate(layoutInflater)
        return binding.root
    }
}