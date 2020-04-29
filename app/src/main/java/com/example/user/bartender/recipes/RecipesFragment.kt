package com.example.user.bartender.recipes

import android.os.Bundle
import android.view.*
import android.widget.Toast
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
import com.example.user.bartender.R
import com.example.user.bartender.database.BartenderDatabase
import com.example.user.bartender.databinding.FragmentRecipesBinding
import com.google.android.material.snackbar.Snackbar


class RecipesFragment : Fragment() {
    //val btAdapter = BluetoothAdapter.getDefaultAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentRecipesBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_recipes, container, false)
        val application = requireNotNull(this.activity).application
        val dataSource = BartenderDatabase.getInstance(application).bartenderDatabaseDao
        val viewModelFactory = RecipesViewModelFactory(dataSource, application)
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(RecipesViewModel::class.java)
        setHasOptionsMenu(true)
        val adapter = RecipeAdapter(RecipeListener { recipe ->
            this.findNavController().navigate(RecipesFragmentDirections.actionSimpleRecipesFragmentToEditFragment(recipe.recipeId, recipe.name))
        }, viewModel)
        //установка начальных состояний фильтров
        binding.simpleFilter.isChecked = true
        binding.layerFilter.isChecked = true

        viewModel.recipes.observe(viewLifecycleOwner, Observer {
            if(it!=null){
                if (it.isNotEmpty()) binding.recipeInformationText.visibility = View.GONE
                else binding.recipeInformationText.visibility = View.VISIBLE
                adapter.submitList(it)
            }
        })

        viewModel.connectionStatus.observe(viewLifecycleOwner, Observer {
            if(it!=""){
                Toast.makeText(context!!,it,Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.ingredients.observe(viewLifecycleOwner, Observer {
            viewModel.connections.observe(viewLifecycleOwner, Observer{ connections ->
                viewModel.buttonID.observe(viewLifecycleOwner, Observer {id->
                    if(id>-1){
                        val ingredients = viewModel.getIngredientArrayList(id, connections)
                        viewModel.isReadyToCook(ingredients)
                    }
                })
            })
        })

        binding.model = viewModel
        binding.recRecyclerView.adapter = adapter
        val manager = LinearLayoutManager(activity)
        binding.recRecyclerView.layoutManager = manager
        binding.lifecycleOwner = this

        binding.addRecipeButton.setOnClickListener{
            this.findNavController().navigate(RecipesFragmentDirections.actionSimpleRecipesFragmentToEditFragment(-1,""))
        }

        binding.simpleFilter.setOnClickListener {
           viewModel.changeFilterState(0)
            viewModel.filter()
           viewModel.recipes.observe(viewLifecycleOwner, Observer {
                if(it!=null){
                    if (it.isNotEmpty()) binding.recipeInformationText.visibility = View.GONE
                    else binding.recipeInformationText.visibility = View.VISIBLE
                    adapter.submitList(it)
                }
            })
        }
        binding.layerFilter.setOnClickListener {
            viewModel.changeFilterState(1)
            viewModel.filter()
            viewModel.recipes.observe(viewLifecycleOwner, Observer {
                if(it!=null){
                    if (it.isNotEmpty())binding.recipeInformationText.visibility = View.GONE
                    else binding.recipeInformationText.visibility = View.VISIBLE
                    adapter.submitList(it)
                }
            })
        }
        //удаление элемента при свайпе
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
            ): Boolean {

                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = adapter.getRecipe(position)
                adapter.notifyDataSetChanged()
                val snackbar = Snackbar.make(view!!,
                        "Вы точно хотите удалить ${item.name}?",
                        Snackbar.LENGTH_LONG)
                snackbar.setAction("Да") {
                        viewModel.delete(item)
                    }
                snackbar.setActionTextColor(resources.getColor(R.color.accent))
                snackbar.show()
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
