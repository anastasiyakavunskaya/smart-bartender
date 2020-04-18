package com.example.user.bartender.settings


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.user.bartender.R
import com.example.user.bartender.databinding.FragmentMotorsBinding


class MotorsFragment : Fragment() {

    private var spinnerArray = ArrayList<Spinner>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentMotorsBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_motors, container, false)

        val application = requireNotNull(this.activity).application
        val viewModel = MotorsViewModel(application)

        binding.lifecycleOwner = this
        spinnerArray = arrayListOf(binding.setting1,binding.setting2,binding.setting3,binding.setting4,binding.setting5,binding.setting6)
        viewModel.motors.observe(viewLifecycleOwner, Observer { settings ->
            viewModel.ingredients.observe(viewLifecycleOwner, Observer { allIngredients ->
                if(allIngredients.isNotEmpty()){
                    for (i in spinnerArray.indices){
                        spinnerArray[i].onItemSelectedListener
                        val aa = ArrayAdapter(this.context!!, android.R.layout.simple_spinner_item, viewModel.getNames(allIngredients))
                        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerArray[i].adapter = aa
                    }
                    if(settings.isNotEmpty()) setMotorsSettings(settings, viewModel.getNames(allIngredients))
                    else setMotorsSettings(List(6){"Пусто"} as ArrayList<String>, viewModel.getNames(allIngredients))
                }
                else{
                    binding.motorsText.text = getString(R.string.empty_ingredient_list_for_motors)
                    binding.spinnersHolder.removeAllViews()
                }
            })
        })

        binding.setMotorsBtn.setOnClickListener {
            val settings = List(6){"Пусто"}.toMutableList()
            for (i in spinnerArray.indices){
                settings[i] = spinnerArray[i].selectedItem as String
            }
            viewModel.onSetClick(settings)
            this.findNavController().navigate(R.id.action_motorsFragment_to_simpleRecipesFragment)
        }
        binding.clearMotorsBtn.setOnClickListener {
            viewModel.onClearClick()
            for (i in spinnerArray.indices){
                spinnerArray[i].setSelection(0)
            }
        }

        return binding.root
    }

    private fun setMotorsSettings(items: ArrayList<String>, allIngredients: ArrayList<String>) {
        for (i in items.indices){
            spinnerArray[i].setSelection(allIngredients.indexOf(items[i]))
        }
    }
}

