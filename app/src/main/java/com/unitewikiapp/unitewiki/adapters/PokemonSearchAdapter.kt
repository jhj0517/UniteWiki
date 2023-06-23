package com.unitewikiapp.unitewiki.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.librarydevloperjo.kcs.KCS
import com.unitewikiapp.unitewiki.databinding.ItemPokemonSearchBinding
import com.unitewikiapp.unitewiki.datas.PokemonSearchData
import com.unitewikiapp.unitewiki.views.searchfragments.SearchMainFragmentDirections

class PokemonSearchAdapter:
    ListAdapter<PokemonSearchData,PokemonSearchAdapter.ViewHolder>(diffUtil), Filterable {

    private var list = arrayListOf<PokemonSearchData>()

    inner class ViewHolder(val binding:ItemPokemonSearchBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(items: PokemonSearchData){
            binding.pokemonName.text = items.pokemon_name
            Glide.with(itemView).load(items.ic_pokemon).into(binding.icPokemon)
        }

        init{
            binding.icPokemon.setOnClickListener {
                val direction = SearchMainFragmentDirections.actionSearchMainFragmentToPokemonInfoFragment(binding.pokemonName.text.toString())
                it.findNavController().navigate(direction)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = ItemPokemonSearchBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun getFilter(): Filter {
        return searchFilter
    }

    fun setData(list: ArrayList<PokemonSearchData>?){
        this.list = list!!
        submitList(list)
    }

    private val searchFilter : Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {

            val filteredList: ArrayList<PokemonSearchData> = ArrayList()
            if (constraint.isEmpty()) {
                filteredList.addAll(list)
            } else {
                val filterPattern = constraint.toString().lowercase().trim { it <= ' '}

                for (item in list) {
                    if(KCS.match(filterPattern,item.pokemon_name!!.lowercase())){
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            submitList(results.values as ArrayList<PokemonSearchData>)
        }
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