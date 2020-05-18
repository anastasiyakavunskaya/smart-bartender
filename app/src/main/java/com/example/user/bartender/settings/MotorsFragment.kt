package com.example.user.bartender.settings


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.user.bartender.R
import com.example.user.bartender.databinding.FragmentMotorsBinding

class MotorsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentMotorsBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_motors, container, false)
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        val viewModel = MotorsViewModel(application)
        val adapter = MotorsAdapter(application)
        val manager = LinearLayoutManager(activity)

        viewModel.ingredients.observe(viewLifecycleOwner, Observer { allIngredients ->
            if(allIngredients.isNotEmpty()){
                binding.motorsList.adapter = adapter
                binding.motorsList.layoutManager = manager
                adapter.allIngredients = allIngredients
                adapter.notifyDataSetChanged()
            } else{
                //предупредить о пустом списке ингредиентов
                binding.motorsText.text = getString(R.string.empty_ingredient_list_for_motors)
                binding.motorsList.removeAllViews()
            }
        })
        binding.setMotorsBtn.setOnClickListener {
            val spinnersStates = adapter.getSpinnersState()
            viewModel.onSetButtonClick(spinnersStates)
            this.findNavController().navigate(R.id.action_motorsFragment_to_simpleRecipesFragment)

        }
        binding.clearMotorsBtn.setOnClickListener {
            viewModel.onClearButtonClick()
            adapter.notifyDataSetChanged()
        }
        return binding.root
    }
}

