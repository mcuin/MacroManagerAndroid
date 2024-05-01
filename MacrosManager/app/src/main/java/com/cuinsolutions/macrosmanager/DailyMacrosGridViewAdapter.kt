package com.cuinsolutions.macrosmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cuinsolutions.macrosmanager.databinding.DailyMacrosGridCellBinding
import org.json.JSONObject
import java.text.DecimalFormat

/**
 * Created by mykalcuin on 8/23/17.
 */


class DailyMacrosGridViewAdapter : RecyclerView.Adapter<DailyMacrosGridViewAdapter.MacrosViewHolder>() {

    private val macrosList: MutableList<MacroCell> = mutableListOf()

    fun setMacroItems(macros: List<MacroCell>) {
        macrosList.clear()
        macrosList.addAll(macros)

        notifyDataSetChanged()
    }

    class MacrosViewHolder(val binding: DailyMacrosGridCellBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(cell: MacroCell) {
            binding.macroTitle = cell.macroName
            binding.macroDaily = cell.macrosDescription

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MacrosViewHolder {
        return MacrosViewHolder(DailyMacrosGridCellBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return macrosList.count()
    }

    override fun onBindViewHolder(holder: MacrosViewHolder, position: Int) {
        holder.bind(macrosList[position])
    }
}