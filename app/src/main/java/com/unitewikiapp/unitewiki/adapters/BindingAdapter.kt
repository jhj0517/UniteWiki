package com.unitewikiapp.unitewiki.adapters

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.unitewikiapp.unitewiki.R
import com.unitewikiapp.unitewiki.datas.LocaleField
import com.unitewikiapp.unitewiki.datas.localized
import com.unitewikiapp.unitewiki.utils.LocaleStore

@BindingAdapter("isVisible")
fun bindIsVisible(view: View, isVisible: Boolean?) {
    view.visibility = if (isVisible==true) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)

    }
}

@BindingAdapter("isLiked")
fun bindIsLiked(view: ImageView, isLiked: Boolean?) {
    if (isLiked!!) {
        view.setImageResource(R.drawable.filled_heart)
    }else{
        view.setImageResource(R.drawable.heart)
    }
}

@BindingAdapter("isSkillSelected")
fun bindIsSkillSelected(view: ImageView, isSelected: Boolean?) {
    val greymatrix = ColorMatrix().apply { setSaturation(0.0f) }
    view.colorFilter = if (isSelected==false) {
        ColorMatrixColorFilter(greymatrix)
    } else {
        null
    }
}

@BindingAdapter("localizedText")
fun bindLocalized(view: TextView, localeField: LocaleField) {
    val currentLocale = LocaleStore(view.context).locale
    view.text = localeField.localized(currentLocale!!)
}


