package com.example.user.bartender.recipes.simple

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
    //var spinnerArray = ArrayList<Spinner>()


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
        if(arguments!=null){
            val args = EditFragmentArgs.fromBundle(arguments!!)
            binding.recipeName.text = Editable.Factory.getInstance().newEditable(args.oldRecipeName)
            //val ingredients = viewModel.getIngredients(args.oldRecipeID)
            //Toast.makeText(context,ingredients.toString(),Toast.LENGTH_SHORT).show()
            arguments = null
        }

        viewModel.ingredients.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                for (i in spinnerArray.indices){
                    spinnerArray[i].onItemSelectedListener
                    val aa = ArrayAdapter(this.context, android.R.layout.simple_spinner_item, viewModel.getNames(it))
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerArray[i].adapter = aa
                }
            }
        })


        binding.recipeSave!!.setOnClickListener {
            val newIngredients = ArrayList<Triple<String, Double, Int>>()
            for(i in spinnerArray.indices){
                val item = spinnerArray[i].selectedItem.toString()
                if ( item != "Пусто"){
                    newIngredients.add(Triple(item,volumeArray[i].text.toString().toDouble(),layerArray[i].text.toString().toInt()))
                }
            }
            viewModel.onSaveButtonClickToAdd(binding.recipeName.text.toString(),newIngredients)
            this.findNavController().navigate(EditFragmentDirections.actionEditFragmentToSimpleRecipesFragment())
        }
        binding.recipeCancel!!.setOnClickListener{
            this.findNavController().navigate(EditFragmentDirections.actionEditFragmentToSimpleRecipesFragment())
        }


        // Inflate the layout for this fragment
        return binding.root
    }


}
