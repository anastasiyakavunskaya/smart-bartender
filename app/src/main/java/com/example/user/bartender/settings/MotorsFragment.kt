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
import com.example.user.bartender.R
import com.example.user.bartender.databinding.FragmentMotorsBinding


class MotorsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
         val binding: FragmentMotorsBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_motors, container, false)
        val application = requireNotNull(this.activity).application
        val viewModel = MotorsViewModel(application)
        binding.setLifecycleOwner(this)
        val spinnerArray: Array<Spinner> = arrayOf(binding.setting1,binding.setting2,binding.setting3,binding.setting4,binding.setting5,binding.setting6)
        viewModel.ingredients.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                for (i in spinnerArray.indices){
                    spinnerArray[i].onItemSelectedListener
                    val aa = ArrayAdapter(this.context, android.R.layout.simple_spinner_item, viewModel.getNames(it))
                    // Set layout to use when the list of choices appear
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    // Set Adapter to Spinner
                    spinnerArray[i].adapter = aa
                }
            }
            else{
                binding.motorsText.text = "В базе данных нет ингредиентов, добавьте их в пункте меню \"Список ингредиентов\""
                binding.spinnersHolder.removeAllViews()
            }
        })
        binding.setMotorsBtn.setOnClickListener {
            val settings = List<Long>(6){-1}.toMutableList()
            for (i in spinnerArray.indices){
                val item = spinnerArray[i].selectedItem as String
                if (item == "Пусто") settings[i] = -1
                else settings[i] = viewModel.getId(item)
            }
            viewModel.onSetClick(settings)
        }

        return binding.root
    }

}

