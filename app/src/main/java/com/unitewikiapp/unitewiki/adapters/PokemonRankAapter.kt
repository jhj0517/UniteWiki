package com.unitewikiapp.unitewiki.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.unitewikiapp.unitewiki.databinding.ItemPokemonrankingBinding
import com.unitewikiapp.unitewiki.datas.PokemonRankData
import com.unitewikiapp.unitewiki.viewmodels.PokemonRankAdapterViewModel
import com.unitewikiapp.unitewiki.views.fragments.MainFragmentDirections


class PokemonRankAdapter:
    ListAdapter<PokemonRankData,PokemonRankAdapter.ViewHolder>(diffUtil){

    inner class ViewHolder(val binding:ItemPokemonrankingBinding): RecyclerView.ViewHolder(binding.root){

        init {
            binding.linearLayout.setOnClickListener {  view ->
                binding.viewModel?.pokemonName?.let { pokemonName->
                    val direction = MainFragmentDirections.actionMainFragmentToPokemonInfoFragment(pokemonName)
                    view.findNavController().navigate(direction)
                }
            }
        }

        fun bind(items:PokemonRankData){
            with(binding){
                viewModel = PokemonRankAdapterViewModel(items)
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPokemonrankingBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.rank.text = (position+1).toString()
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PokemonRankData>() {
            override fun areContentsTheSame(oldItem: PokemonRankData, newItem: PokemonRankData) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: PokemonRankData, newItem: PokemonRankData) =
                oldItem.pokemon_name == newItem.pokemon_name
        }
    }
}