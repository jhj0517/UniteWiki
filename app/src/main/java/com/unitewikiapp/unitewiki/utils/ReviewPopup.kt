package com.unitewikiapp.unitewiki.utils

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import com.unitewikiapp.unitewiki.datas.PokemonReviewsData

class ReviewPopup {

    interface ClickCallback{
        fun onPopupMenuItemClick(itemId: Int, position:Int, itemData: PokemonReviewsData?, anchor: View)
    }

    companion object {
        fun showMenu(context: Context,
                     anchor: View,
                     @MenuRes menuRes: Int,
                     review: PokemonReviewsData?,
                     position: Int,
                     clickCallback: ClickCallback) {
            val popup = PopupMenu(context, anchor)
            popup.inflate(menuRes)
            popup.setOnMenuItemClickListener { item ->
                clickCallback.onPopupMenuItemClick(item.itemId,position, review, anchor)
                false
            }
            popup.show()
        }
    }
}