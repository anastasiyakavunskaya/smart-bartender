package com.example.user.bartender.recipes.simple

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.user.bartender.database.Recipe
import com.example.user.bartender.databinding.ItemRecipeBinding


class SimpleRecipeAdapter(val clickListener: RecipeListener): ListAdapter<Recipe, SimpleRecipeAdapter.ViewHolder>(RecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int):ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    fun getRecipe(position: Int) = getItem(position)

    class ViewHolder private constructor(val binding: ItemRecipeBinding): RecyclerView.ViewHolder(binding.root){
        fun bind (item: Recipe, clickListener: RecipeListener){
            binding.recipe = item
            binding.clickListener = clickListener
            binding.executePendingBindings()

        }
        companion object{
            fun from(parent: ViewGroup):ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemRecipeBinding.inflate(layoutInflater)
                return ViewHolder(binding)
            }
        }
    }
}
class RecipeDiffCallback: DiffUtil.ItemCallback<Recipe>(){
    override fun areItemsTheSame(oldRecipe: Recipe, newRecipe: Recipe): Boolean {
        return oldRecipe.recipeId == newRecipe.recipeId
    }

    override fun areContentsTheSame(oldRecipe: Recipe, newRecipe: Recipe): Boolean {
        return oldRecipe == newRecipe
    }

}
class RecipeListener(val clickListener: (recipe: Recipe) -> Unit){
    fun onClick(recipe: Recipe) = clickListener(recipe)
}
