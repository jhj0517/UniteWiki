package com.unitewikiapp.unitewiki.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.librarydevloperjo.kcs.KCS
import com.unitewikiapp.unitewiki.databinding.ItemPokemonSearchBinding
import com.unitewikiapp.unitewiki.datas.PokemonInfoData
import com.unitewikiapp.unitewiki.datas.localized
import com.unitewikiapp.unitewiki.utils.LocaleStore
import com.unitewikiapp.unitewiki.viewmodels.PokemonSearchAdapterViewModel
import com.unitewikiapp.unitewiki.views.searchfragments.SearchMainFragmentDirections

class PokemonSearchAdapter(
    localeStore: LocaleStore
): ListAdapter<PokemonInfoData,PokemonSearchAdapter.ViewHolder>(diffUtil), Filterable {

    private var list = arrayListOf<PokemonInfoData>()

    inner class ViewHolder(val binding:ItemPokemonSearchBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(items: PokemonInfoData){
            with(binding){
                viewModel = PokemonSearchAdapterViewModel(items)
                executePendingBindings()
            }
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

    fun setData(list: ArrayList<PokemonInfoData>?){
        this.list = list!!
        submitList(list)
    }

    private val searchFilter : Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {

            val filteredList: ArrayList<PokemonInfoData> = ArrayList()
            if (constraint.isEmpty()) {
                filteredList.addAll(list)
            } else {
                val filterPattern = constraint.toString().lowercase().trim { it <= ' '}

                for (item in list) {
                    if(KCS.match(filterPattern,item.pokemon_name.localized(localeStore.locale!!).lowercase())){
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            submitList(results.values as ArrayList<PokemonInfoData>)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PokemonInfoData>() {
            override fun areContentsTheSame(oldItem: PokemonInfoData, newItem: PokemonInfoData) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: PokemonInfoData, newItem: PokemonInfoData) =
                oldItem.pokemon_name == newItem.pokemon_name
        }
    }
}