package com.example.user.bartender.ingredients

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.user.bartender.R
import com.example.user.bartender.database.BartenderDatabase
import com.example.user.bartender.database.Ingredient
import com.example.user.bartender.databinding.FragmentEditIngredientBinding

class EditIngredientFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentEditIngredientBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_edit_ingredient, container, false)
        val application = requireNotNull(this.activity).application
        val dataSource = BartenderDatabase.getInstance(application).bartenderDatabaseDao

        val viewModelFactory = IngredientsViewModelFactory(dataSource, application)
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(IngredientsViewModel::class.java)
        binding.ingredientC.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.result.text = progress.toString()
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        val args = EditIngredientFragmentArgs.fromBundle(arguments!!)
        val oldIngredient = Ingredient(args.ingredientName, args.ingredientCoefficient)
        binding.ingredientName.text = Editable.Factory.getInstance().newEditable(args.ingredientName)
        binding.ingredientC.progress = args.ingredientCoefficient
        binding.saveButton.setOnClickListener {
            val name = binding.ingredientName.text
            if(!name.isNullOrBlank()) {
                viewModel.onSaveButtonClick(oldIngredient, name.toString(), binding.ingredientC.progress)
                this.findNavController().navigate(EditIngredientFragmentDirections.actionEditIngredientFragmentToIngredientsFragment())
            }else Toast.makeText(this.context,"Введите название ингредиента", Toast.LENGTH_SHORT).show()
        }
        binding.cancelButton.setOnClickListener {
            this.findNavController().navigate(EditIngredientFragmentDirections.actionEditIngredientFragmentToIngredientsFragment())
        }
        binding.informationButton.setOnClickListener {
            binding.informationText.visibility = View.VISIBLE
        }
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        val imm: InputMethodManager = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

}
