package com.unitewikiapp.unitewiki.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.unitewikiapp.unitewiki.databinding.LetterSignBinding
import com.unitewikiapp.unitewiki.datas.PokemonSearchData

class SearchListParentAdapter(private val context: Context)
    : ListAdapter<PokemonSearchData,SearchListParentAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(val binding: LetterSignBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(items: PokemonSearchData) {
            binding.Letter.text = items.letter
            val adapter = PokemonSearchAdapter()
            adapter.setData(items.childList)
            binding.ChildRecyclerView.adapter = adapter
            binding.ChildRecyclerView.layoutManager = GridLayoutManager(context, 5)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = LetterSignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PokemonSearchData>() {
            override fun areContentsTheSame(oldItem: PokemonSearchData, newItem: PokemonSearchData) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: PokemonSearchData, newItem: PokemonSearchData) =
                oldItem.pokemon_name == newItem.pokemon_name
        }
    }
}