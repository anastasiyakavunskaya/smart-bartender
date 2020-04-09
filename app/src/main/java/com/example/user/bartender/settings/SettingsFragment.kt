package com.example.user.bartender.settings


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.user.bartender.R
import com.example.user.bartender.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentSettingsBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_settings, container, false)
        //binding.infoWebView.loadUrl("file:///android_asset/settings_info.html")

        binding.setLifecycleOwner(this)
        binding.aboutButton.setOnClickListener {
            this.findNavController().navigate(R.id.action_settingsFragment_to_infoFragment)
        }
        binding.ingredientsButton.setOnClickListener {
            this.findNavController().navigate(R.id.action_settingsFragment_to_ingredientsFragment)
        }
        binding.motorsButton.setOnClickListener {
            this.findNavController().navigate(R.id.action_settingsFragment_to_motorsFragment)
        }
        return binding.root
    }
}
