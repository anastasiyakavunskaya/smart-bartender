package com.example.user.bartender.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.user.bartender.R
import com.example.user.bartender.databinding.FragmentMenuBinding


class MenuFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentMenuBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_menu, container, false)

        binding.setLifecycleOwner(this)

        binding.recipesButton!!.setOnClickListener {
            this.findNavController().navigate(R.id.action_menuFragment_to_simpleRecipesFragment)
        }

        binding.settingsButton.setOnClickListener {
            this.findNavController().navigate(R.id.action_menuFragment_to_settingsFragment)
        }

        binding.infoButton.setOnClickListener {
            this.findNavController().navigate(R.id.action_menuFragment_to_infoFragment)
        }

    return binding.root
    }
}
