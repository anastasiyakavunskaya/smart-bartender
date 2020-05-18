package com.example.user.bartender.settings

import android.R
import android.app.Application
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.user.bartender.SmartBartender
import com.example.user.bartender.database.Ingredient
import com.example.user.bartender.databinding.ItemMotorBinding

class MotorsAdapter(application: Application):RecyclerView.Adapter<MotorsAdapter.MotorsViewHolder>() {
    private val smartBartender = application as SmartBartender
    var allIngredients = emptyList<Ingredient>()
    private val viewHolders = ArrayList<MotorsViewHolder?>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MotorsViewHolder {
        val viewHolder = MotorsViewHolder.from(parent)
        viewHolders.add(viewHolder)
        return viewHolder
    }
    override fun getItemCount(): Int = smartBartender.getMotors().size
    override fun onBindViewHolder(holder: MotorsViewHolder, position: Int) {
        val settings = smartBartender.getMotors()
        holder.bind(position, settings, allIngredients)
    }
    fun getSpinnersState():ArrayList<String>{
        val spinnerStateList = arrayListOf("Пусто", "Пусто", "Пусто", "Пусто", "Пусто", "Пусто")
        //TODO FIX THIS SHIT
        if(viewHolders.size>6) viewHolders.removeAt(0)
        for(i in 0 ..5) {
            val viewHolder = viewHolders[i]!!
            spinnerStateList[i] = viewHolder.getState()
        }
        return spinnerStateList
    }
    class MotorsViewHolder(val binding: ItemMotorBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int, settings: List<Ingredient>, list: List<Ingredient>){
            val number = position+1
            binding.motorsCount.text = "МОТОР $number"
            val aa = ArrayAdapter(binding.root.context, R.layout.simple_spinner_item, convertIngredientListToStringArray(list))
            aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spinner.adapter = aa
            binding.spinner.setSelection(list.indexOf(settings[position])+1)
        }

        companion object{
            fun from(parent:ViewGroup): MotorsViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemMotorBinding.inflate(layoutInflater)
                return MotorsViewHolder(binding)
            }
        }

        private fun convertIngredientListToStringArray(list: List<Ingredient>): ArrayList<String> {
            val array = ArrayList<String>()
            array.add(0, "Пусто")
            for (i in 1 until list.size+1)
                array.add(list[i-1].name)
            return array
        }

        fun getState(): String{
            val selectedItem = binding.spinner.selectedItem as String
            return selectedItem
        }
    }
}
