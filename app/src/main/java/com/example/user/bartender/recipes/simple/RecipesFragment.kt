package com.example.user.bartender.recipes.simple

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.user.bartender.R
import com.example.user.bartender.database.BartenderDatabase
import com.example.user.bartender.databinding.FragmentRecipesBinding

class RecipesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentRecipesBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_recipes, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = BartenderDatabase.getInstance(application).bartenderDatabaseDao

        val viewModelFactory = RecipesViewModelFactory(dataSource, application)
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(RecipesViewModel::class.java)

        val adapter = SimpleRecipeAdapter(RecipeListener {
            recipe -> viewModel.onItemClick(recipe)
        })

        binding.recRecyclerView.adapter = adapter
        val manager = LinearLayoutManager(activity)
        binding.recRecyclerView.layoutManager = manager
        binding.setLifecycleOwner(this)

        viewModel.recipes.observe(viewLifecycleOwner, Observer {
            if(it!=null){
                if (it.isEmpty()) binding.recipeInformationText.text = getString(R.string.empty_list_of_recipes)
                else binding.recipeInformationText.text = getString(R.string.ingredients_information_recipes)
                adapter.submitList(it)
            }
        })
        viewModel.editRecipe.observe(viewLifecycleOwner, Observer {
            if(it!=null) this.findNavController().navigate(RecipesFragmentDirections.actionSimpleRecipesFragmentToEditFragment(it.recipeId, it.name))
        })

        binding.addRecipeButton.setOnClickListener {
            this.findNavController().navigate(RecipesFragmentDirections.actionSimpleRecipesFragmentToEditFragment(-1,""))
        }


        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
            ): Boolean {

                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewModel.delete(adapter.getRecipe(viewHolder.adapterPosition))
            }

        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recRecyclerView)
        return binding.root
    }
}
