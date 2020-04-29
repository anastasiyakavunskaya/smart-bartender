package com.example.user.bartender.ingredients

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
import com.example.user.bartender.databinding.FragmentIngredientsBinding
import com.google.android.material.snackbar.Snackbar

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
            this.findNavController().navigate(IngredientsFragmentDirections.actionIngredientsFragmentToEditIngredientFragment(it.name, it.c))
        })

        binding.ingRecyclerView.adapter = adapter
        val manager = LinearLayoutManager(activity)
        binding.ingRecyclerView.layoutManager = manager
        binding.lifecycleOwner = this

        binding.addIngredientButton.setOnClickListener{
            this.findNavController().navigate(IngredientsFragmentDirections.actionIngredientsFragmentToEditIngredientFragment("", 1500))
        }

        viewModel.ingredients.observe(viewLifecycleOwner, Observer {
            if(it!=null){
                if (it.isNotEmpty()) binding.ingredientsInformationText.visibility = View.GONE
                adapter.submitList(it)
            }
        })

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = adapter.getIngredient(position)
                val snackBar = Snackbar.make(view!!,
                        "Вы точно хотите удалить ${item.name}?",
                        Snackbar.LENGTH_LONG)
                snackBar.setAction("Да") {
                    viewModel.delete(item)
                    snackBar.dismiss()
                }
                snackBar.setActionTextColor(resources.getColor(R.color.accent))
                snackBar.show()
                adapter.notifyDataSetChanged()
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.ingRecyclerView)
        return binding.root
    }
}
