package com.example.user.bartender.recipes

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.user.bartender.MainActivity
import com.example.user.bartender.R
import com.example.user.bartender.bluetooth.BluetoothController
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
        setHasOptionsMenu(true)

        binding.model = viewModel
        viewModel.connections.observe(viewLifecycleOwner, Observer{ connections ->
        viewModel.buttonID.observe(viewLifecycleOwner, Observer {id->
            if(id>-1){
                    val ingredients = viewModel.getIngredients(id,connections)
                    viewModel.isActive(ingredients)
            }
        })
        })

        binding.filterSimple.isChecked = true
        binding.filterLayer.isChecked = true

        BluetoothController(this.activity as MainActivity).start()

        val adapter = RecipeAdapter(RecipeListener { recipe ->
            this.findNavController().navigate(RecipesFragmentDirections.actionSimpleRecipesFragmentToEditFragment(recipe.recipeId, recipe.name))
        },viewModel)
        binding.recRecyclerView.adapter = adapter
        val manager = LinearLayoutManager(activity)
        binding.recRecyclerView.layoutManager = manager
        binding.lifecycleOwner = this

        viewModel.recipes.observe(viewLifecycleOwner, Observer {
            if(it!=null){
                if (it.isNotEmpty())binding.recipeInformationText.visibility = View.GONE
                else binding.recipeInformationText.visibility = View.VISIBLE
                adapter.submitList(it)
            }
        })

       /* viewModel.simpleFilter.observe(viewLifecycleOwner, Observer{ filter->
            binding.filterSimple.isChecked = filter
        })

        viewModel.layerFilter.observe(viewLifecycleOwner, Observer{ filter->
            binding.filterLayer.isChecked = filter
        })*/

        /*viewModel.recipes.observe(viewLifecycleOwner, Observer {
            if(it!=null){
                if (it.isNotEmpty())binding.recipeInformationText.visibility = View.GONE
                else binding.recipeInformationText.visibility = View.VISIBLE
                adapter.submitList(it)
            }
        })*/

        binding.addRecipeButton.setOnClickListener{
            this.findNavController().navigate(RecipesFragmentDirections.actionSimpleRecipesFragmentToEditFragment(-1,""))
        }
        binding.filterSimple.setOnClickListener {
           viewModel.filterSimple()
           viewModel.recipes.observe(viewLifecycleOwner, Observer {
                if(it!=null){
                    if (it.isNotEmpty()) binding.recipeInformationText.visibility = View.GONE
                    else binding.recipeInformationText.visibility = View.VISIBLE
                    adapter.submitList(it)
                }
            })
        }
        binding.filterLayer.setOnClickListener {
            viewModel.filterLayer()
            viewModel.recipes.observe(viewLifecycleOwner, Observer {
                if(it!=null){
                    if (it.isNotEmpty())binding.recipeInformationText.visibility = View.GONE
                    else binding.recipeInformationText.visibility = View.VISIBLE
                    adapter.submitList(it)
                }
            })
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return NavigationUI.onNavDestinationSelected(item!!,
                view!!.findNavController())
                || super.onOptionsItemSelected(item)
    }
}
