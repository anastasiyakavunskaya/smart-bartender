package com.example.user.bartender

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.user.bartender.database.Recipe

@BindingAdapter("recipeIcon")
fun ImageView.set(item: Recipe) {
    setImageResource(when (item.type) {
        "simple" -> R.drawable.simple
        "layer" -> R.drawable.layer
        else -> R.drawable.ic_launcher_foreground
    })
}
