
package com.example.user.bartender.recipes

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.user.bartender.R
import com.example.user.bartender.database.BartenderDatabase
import com.example.user.bartender.databinding.FragmentEditRecipesBinding



class EditFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentEditRecipesBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_edit_recipes, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = BartenderDatabase.getInstance(application).bartenderDatabaseDao

        val viewModelFactory = RecipesViewModelFactory(dataSource, application)
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(RecipesViewModel::class.java)

        val spinnerArray = arrayOf(binding.ing1,binding.ing2,binding.ing3,binding.ing4,binding.ing5,binding.ing6)
        val volumeArray = arrayOf(binding.value1,binding.value2,binding.value3,binding.value4,binding.value5,binding.value6)
        val layerArray = arrayOf(binding.layer1,binding.layer2,binding.layer3,binding.layer4,binding.layer5,binding.layer6)

        val args = EditFragmentArgs.fromBundle(arguments!!)

        viewModel.ingredients.observe(viewLifecycleOwner, Observer {allIngredients ->
            for (i in spinnerArray.indices){
                    spinnerArray[i].onItemSelectedListener
                    val aa = ArrayAdapter(this.context!!, android.R.layout.simple_spinner_item, viewModel.getNames(allIngredients))
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerArray[i].adapter = aa
                }

                if(args.oldRecipeID>-1){
                    binding.recipeName.text = Editable.Factory.getInstance().newEditable(args.oldRecipeName)
                    viewModel.connections.observe(viewLifecycleOwner, Observer {currentConnections ->
                        if (currentConnections.isNotEmpty())
                        {
                            val ingredientsList = viewModel.getIngredients(args.oldRecipeID, currentConnections)
                            for(i in ingredientsList.indices){
                                spinnerArray[i].setSelection(viewModel.getNames(allIngredients).indexOf(ingredientsList[i].first))
                                volumeArray[i].text = Editable.Factory.getInstance().newEditable(ingredientsList[i].second.toString())
                                layerArray[i].text = Editable.Factory.getInstance().newEditable(ingredientsList[i].third.toString())
                            }
                        }
                    })
            }
        })

        binding.recipeSave.setOnClickListener {
            val ingredients = ArrayList<Triple<String, Double, Int>>()
            val newName = binding.recipeName.text.toString()
            for(i in spinnerArray.indices){
                val ingredientName = spinnerArray[i].selectedItem.toString()
                if (ingredientName != "Пусто"){
                    val volume = volumeArray[i].text.toString().toDouble()
                    val layer = layerArray[i].text.toString().toInt()
                    ingredients.add(Triple(ingredientName, volume , layer))
                }
            }
            if(args.oldRecipeID>-1){
                val recipeID = EditFragmentArgs.fromBundle(arguments!!).oldRecipeID
                viewModel.onSaveButtonClickToEdit(recipeID, newName, ingredients)
                arguments = null
            }
            else viewModel.onSaveButtonClickToAdd(newName, ingredients)
            this.findNavController().navigate(EditFragmentDirections.actionEditFragmentToSimpleRecipesFragment())
        }
        binding.recipeCancel.setOnClickListener{
            this.findNavController().navigate(EditFragmentDirections.actionEditFragmentToSimpleRecipesFragment())
        }
        return binding.root
    }
}
