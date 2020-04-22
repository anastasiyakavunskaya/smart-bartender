package com.example.user.bartender.recipes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.user.bartender.database.Recipe
import com.example.user.bartender.databinding.ItemRecipeBinding


class RecipeAdapter(private val clickListener: RecipeListener, val model: RecipesViewModel): ListAdapter<Recipe, RecipeAdapter.ViewHolder>(RecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int):ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener, model)
    }

    fun getRecipe(position: Int): Recipe = getItem(position)
    class ViewHolder private constructor(val binding: ItemRecipeBinding): RecyclerView.ViewHolder(binding.root){
        fun bind (item: Recipe, clickListener: RecipeListener, model: RecipesViewModel){
            binding.recipe = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
            binding.model = model

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
