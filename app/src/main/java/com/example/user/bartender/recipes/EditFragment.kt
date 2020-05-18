
package com.example.user.bartender.recipes

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
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

        val spinnerArray = arrayOf(
                binding.ingredient1,
                binding.ingredient2,
                binding.ingredient3,
                binding.ingredient4,
                binding.ingredient5,
                binding.ingredient6)
        val volumeArray = arrayOf(
                binding.volume1,
                binding.volume2,
                binding.volume3,
                binding.volume4,
                binding.volume5,
                binding.volume6)
        val layerArray = arrayOf(
                binding.layer1,
                binding.layer2,
                binding.layer3,
                binding.layer4,
                binding.layer5,
                binding.layer6)

        val args = EditFragmentArgs.fromBundle(arguments!!)

        viewModel.ingredientsLiveData.observe(viewLifecycleOwner, Observer { allIngredients ->
            //установка списка ингредиентов в Spinner'ы
            for (i in spinnerArray.indices){
                    spinnerArray[i].onItemSelectedListener
                    val aa = ArrayAdapter(this.context!!, android.R.layout.simple_spinner_item, viewModel.convertIngredientListToStringArray(allIngredients))
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerArray[i].adapter = aa
            }
            //установка данных о изменяемом/добавляемом рецепте
            if(args.oldRecipeID>-1){
                    binding.recipeName.text = Editable.Factory.getInstance().newEditable(args.oldRecipeName)
                    viewModel.connectionsLiveData.observe(viewLifecycleOwner, Observer { currentConnections ->
                        if (currentConnections.isNotEmpty()) {
                            val ingredientsList = viewModel.getIngredientArrayList(args.oldRecipeID, currentConnections)
                            for(i in ingredientsList.indices){
                                spinnerArray[i].setSelection(viewModel.convertIngredientListToStringArray(allIngredients).indexOf(ingredientsList[i].first.name))
                                volumeArray[i].text = Editable.Factory.getInstance().newEditable(ingredientsList[i].second.toString())
                                layerArray[i].text = Editable.Factory.getInstance().newEditable(ingredientsList[i].third.toString())
                            }
                        }
                    })
            }
        })

        binding.recipeSave.setOnClickListener {
            val ingredients = ArrayList<Triple<String, Double, Int>>()
            var dataStatus = ""
            var recipeName = ""
            if(binding.recipeName.length()!=0) {
                //получаем имя рецепта
                recipeName = binding.recipeName.text.toString()
                //получаем ингредиенты
                for(i in spinnerArray.indices){
                    val ingredientName = spinnerArray[i].selectedItem.toString()
                    if (ingredientName != "Пусто"){
                        if(!volumeArray[i].text.isNullOrBlank()&&!layerArray[i].text.isNullOrBlank()) {
                            val volume = volumeArray[i].text.toString().toDouble()
                            val layer = layerArray[i].text.toString().toInt()
                            if(layer in 1..6) ingredients.add(Triple(ingredientName, volume, layer))
                            else dataStatus += "Номер слоя не соответствует диапазону от 1 до 6"
                        } else dataStatus += "Необходимо ввести объём ингредиента и его слой в коктейле"
                    }
                }
            } else dataStatus +="Необходимо ввести название рецепта"
             if(dataStatus.isEmpty()){
                 val recipeID = EditFragmentArgs.fromBundle(arguments!!).oldRecipeID
                 viewModel.onSaveButtonClick(recipeID, recipeName, ingredients)
                 this.findNavController().navigate(EditFragmentDirections.actionEditFragmentToSimpleRecipesFragment())
             } else Toast.makeText(this.context, dataStatus, Toast.LENGTH_SHORT).show()
        }
        binding.recipeCancel.setOnClickListener{
            this.findNavController().navigate(EditFragmentDirections.actionEditFragmentToSimpleRecipesFragment())
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        val imm: InputMethodManager = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }
}
