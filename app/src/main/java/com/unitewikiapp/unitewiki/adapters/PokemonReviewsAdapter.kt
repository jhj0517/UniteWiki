package com.unitewikiapp.unitewiki.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.unitewikiapp.unitewiki.databinding.ItemPokemonReveiwsBinding
import com.unitewikiapp.unitewiki.datas.PokemonReviewsData
import com.unitewikiapp.unitewiki.viewmodels.PokemonReviewAdapterViewModel

class PokemonReviewsAdapter (private val isMyReview: Boolean, val clickCallBack:ClickCallback):
    ListAdapter<PokemonReviewsData, PokemonReviewsAdapter.ReviewViewHolder>(diffUtil){

    interface ClickCallback{
        fun onClickPopupEditMenu(position: Int, itemData:PokemonReviewsData?, anchor:ImageView)
        fun onClickPopupReportMenu(position: Int, itemData:PokemonReviewsData?, anchor:ImageView)
        fun onClickLikeButton(position: Int, itemData:PokemonReviewsData?, likeView:ImageView)
    }

    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<PokemonReviewsData>() {
            override fun areContentsTheSame(oldItem: PokemonReviewsData, newItem: PokemonReviewsData) =
                oldItem == newItem
            override fun areItemsTheSame(oldItem: PokemonReviewsData, newItem: PokemonReviewsData) =
                oldItem == newItem
        }
    }

    inner class ReviewViewHolder(val reviewBinding:ItemPokemonReveiwsBinding):RecyclerView.ViewHolder(reviewBinding.root) {

        fun reviewBind(items: PokemonReviewsData) {
            with(reviewBinding){
                viewModel = PokemonReviewAdapterViewModel(items)
                executePendingBindings()
                isLiked = items.isLiked!!
                isEdited = items.edited
            }
        }

        init {
            var isLoadMoreClicked= false
            reviewBinding.aReviewRoot.setOnClickListener {
                when(isLoadMoreClicked){
                    false -> {reviewBinding.writing.maxLines = 200
                        isLoadMoreClicked = true}
                    true -> {reviewBinding.writing.maxLines = 5
                        isLoadMoreClicked = false}
                }
            }

            reviewBinding.reportMenuRoot.setOnClickListener {
                if (isMyReview) {
                    clickCallBack.onClickPopupEditMenu(bindingAdapterPosition, getItem(bindingAdapterPosition), reviewBinding.reportMenu)
                } else {
                    clickCallBack.onClickPopupReportMenu(bindingAdapterPosition, getItem(bindingAdapterPosition), reviewBinding.reportMenu)
                }
            }

            reviewBinding.layoutLike.setOnClickListener {
                clickCallBack.onClickLikeButton(bindingAdapterPosition, getItem(bindingAdapterPosition), reviewBinding.icLike)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ReviewViewHolder {
        val binding = ItemPokemonReveiwsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.reviewBind(getItem(position))
    }

}