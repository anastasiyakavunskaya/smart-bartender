package com.example.user.bartender.ingredients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.user.bartender.database.Ingredient
import com.example.user.bartender.databinding.ItemIngredientBinding

import com.example.user.bartender.ingredients.IngredientsAdapter.ViewHolder

class IngredientsAdapter (private val clickListener: IngredientListener) : ListAdapter<Ingredient, ViewHolder>(IngredientDiffCallback()){

override fun onCreateViewHolder(parent: ViewGroup, position: Int):ViewHolder {
        return ViewHolder.from(parent)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder.bind(getItem(position), clickListener)
    }

    fun getIngredient(position: Int): Ingredient = getItem(position)

    class ViewHolder private constructor(val binding: ItemIngredientBinding): RecyclerView.ViewHolder(binding.root){
 fun bind (item: Ingredient, clickListener: IngredientListener){
            binding.ingredient = item
            binding.clickListener = clickListener
            binding.executePendingBindings()

        }
        companion object{
            fun from(parent:ViewGroup):ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemIngredientBinding.inflate(layoutInflater)
                return ViewHolder(binding)
            }
        }

    }
}
class IngredientDiffCallback: DiffUtil.ItemCallback<Ingredient>(){
    override fun areItemsTheSame(oldIngedient: Ingredient, newIngredient: Ingredient): Boolean {
        return oldIngedient.ingredientId == newIngredient.ingredientId
    }

    override fun areContentsTheSame(oldIngredient: Ingredient, newIngredient: Ingredient): Boolean {
        return oldIngredient == newIngredient
    }

}
class IngredientListener(val clickListener: (ingredient:Ingredient) -> Unit){
    fun onClick(ingredient: Ingredient) = clickListener(ingredient)

}
