package com.unitewikiapp.unitewiki.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.unitewikiapp.unitewiki.databinding.ItemPokemonReveiwsBinding
import com.unitewikiapp.unitewiki.datas.PokemonReviewsData
import com.unitewikiapp.unitewiki.viewmodels.PokemonReviewAdapterViewModel
import com.unitewikiapp.unitewiki.views.fragments.PokemonInfoFragmentDirections


class PokemonReviewInfoAdapter:
    ListAdapter<PokemonReviewsData, PokemonReviewInfoAdapter.ReviewViewHolder>(diffUtil){

    private lateinit var auth: FirebaseAuth

    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<PokemonReviewsData>() {
            override fun areContentsTheSame(oldItem: PokemonReviewsData, newItem: PokemonReviewsData) =
                oldItem == newItem
            override fun areItemsTheSame(oldItem: PokemonReviewsData, newItem: PokemonReviewsData) =
                oldItem == newItem
        }
    }

    inner class ReviewViewHolder(private val reviewBinding: ItemPokemonReveiwsBinding): RecyclerView.ViewHolder(reviewBinding.root) {
        fun reviewBind(items: PokemonReviewsData) {
            with(reviewBinding){
                viewModel = PokemonReviewAdapterViewModel(items)
                executePendingBindings()
            }
        }

        init {
            reviewBinding.aReviewRoot.setOnClickListener { view->
                reviewBinding.viewModel?.pokemon?.let { pokemon->
                    val direction = PokemonInfoFragmentDirections.actionPokemonInfoFragmentToPokemonReviewsFragment(pokemon)
                    view.findNavController().navigate(direction)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonReviewInfoAdapter.ReviewViewHolder {
        auth = Firebase.auth
        val binding = ItemPokemonReveiwsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.reviewBind(currentList[position])
    }

}