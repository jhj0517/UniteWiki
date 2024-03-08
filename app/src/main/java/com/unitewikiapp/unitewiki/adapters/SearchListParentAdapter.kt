package com.unitewikiapp.unitewiki.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.unitewikiapp.unitewiki.databinding.LetterSignBinding
import com.unitewikiapp.unitewiki.datas.SearchListParentData
import com.unitewikiapp.unitewiki.utils.LocaleStore
import com.unitewikiapp.unitewiki.viewmodels.SearchListParentAdapterViewModel

class SearchListParentAdapter(
    private val context: Context,
    private val localeStore: LocaleStore
) : ListAdapter<SearchListParentData,SearchListParentAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(val binding: LetterSignBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(items: SearchListParentData) {
            with(binding){
                viewModel =  SearchListParentAdapterViewModel(items)
                executePendingBindings()
            }
            val adapter = PokemonSearchAdapter(localeStore)
            binding.ChildRecyclerView.adapter = adapter
            binding.ChildRecyclerView.layoutManager = GridLayoutManager(context, 5)
            adapter.setData(items.childList)
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
        val diffUtil = object : DiffUtil.ItemCallback<SearchListParentData>() {
            override fun areContentsTheSame(oldItem: SearchListParentData, newItem: SearchListParentData) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: SearchListParentData, newItem: SearchListParentData) =
                oldItem == newItem
        }
    }
}