package com.example.user.bartender.recipes

import android.app.Application
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.user.bartender.SmartBartender
import com.example.user.bartender.database.Ingredient
import com.example.user.bartender.databinding.ItemRecipeBinding


class RecipeAdapter(private val clickListener: RecipeListener, val model: RecipesViewModel, application: Application): ListAdapter<ExtendedRecipe, RecipeAdapter.ViewHolder>(RecipeDiffCallback()) {

    private val smartBartender = application as SmartBartender

    override fun onCreateViewHolder(parent: ViewGroup, position: Int):ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val settings = smartBartender.getMotors()
        holder.bind(getItem(position), clickListener, model, settings)
    }

    fun getRecipe(position: Int): ExtendedRecipe = getItem(position)


    class ViewHolder private constructor(val binding: ItemRecipeBinding): RecyclerView.ViewHolder(binding.root){
        fun bind (item: ExtendedRecipe, clickListener: RecipeListener, model: RecipesViewModel, settings: ArrayList<Ingredient>){
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
class RecipeDiffCallback: DiffUtil.ItemCallback<ExtendedRecipe>(){
    override fun areItemsTheSame(oldRecipe:ExtendedRecipe, newRecipe: ExtendedRecipe): Boolean {
        return oldRecipe.recipe.recipeId == newRecipe.recipe.recipeId
    }

    override fun areContentsTheSame(oldRecipe: ExtendedRecipe, newRecipe: ExtendedRecipe): Boolean {
        return oldRecipe == newRecipe
    }

}
class RecipeListener(val clickListener: (recipe: ExtendedRecipe) -> Unit){
    fun onClick(recipe: ExtendedRecipe) = clickListener(recipe)
}
