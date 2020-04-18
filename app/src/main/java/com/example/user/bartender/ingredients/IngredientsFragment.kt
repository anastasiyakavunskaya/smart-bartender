package com.example.user.bartender.ingredients

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.user.bartender.R
import com.example.user.bartender.database.BartenderDatabase
import com.example.user.bartender.databinding.FragmentIngredientsBinding

class IngredientsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentIngredientsBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_ingredients, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = BartenderDatabase.getInstance(application).bartenderDatabaseDao

        val viewModelFactory = IngredientsViewModelFactory(dataSource, application)
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(IngredientsViewModel::class.java)

        val adapter = IngredientsAdapter(IngredientListener {
            ingredient -> viewModel.onItemClick(ingredient)
        })

        binding.ingRecyclerView.adapter = adapter
        val manager = LinearLayoutManager(activity)
        binding.ingRecyclerView.layoutManager = manager
        binding.lifecycleOwner = this

        viewModel.editItem.observe(viewLifecycleOwner, Observer { oldIngredient ->
            if(oldIngredient!=null){
                binding.ingredientName.text = Editable.Factory.getInstance().newEditable(oldIngredient.name)
                binding.ingredientC.text = Editable.Factory.getInstance().newEditable(oldIngredient.c.toString())
                binding.addBtn.text = "Изменить"
                binding.addBtn.setOnClickListener {
                    viewModel.onEditClick(oldIngredient, requireNotNull(binding.ingredientName.text.toString()), binding.ingredientC.text.toString().toDouble())
                }
            }
            else {
                binding.ingredientName.text = null
                binding.ingredientC.text = null
                binding.addBtn.text = "Сохранить"
                binding.addBtn.setOnClickListener {
                    viewModel.onAddClick(requireNotNull(binding.ingredientName.text.toString()), binding.ingredientC.text.toString().toDouble())
                }
            }
        })

        viewModel.ingredients.observe(viewLifecycleOwner, Observer {
            if(it!=null){
                if (it.isNotEmpty()) binding.ingredientsInformationText.visibility = View.GONE
                adapter.submitList(it)
            }
        })

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                    override fun onMove(
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder
                    ): Boolean {

                        return false
                    }
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        viewModel.delete(adapter.getIngredient(viewHolder.adapterPosition))
                    }

                }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.ingRecyclerView)
        return binding.root
    }
}
