package com.unitewikiapp.unitewiki.views.dialogfragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.unitewikiapp.unitewiki.databinding.DialogLanguageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageDialogFragment : DialogFragment(){

    lateinit var onLanguageSelected: OnLanguageSelected

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val binding =  DialogLanguageBinding.inflate(LayoutInflater.from(context))

            binding.koreanClickLayout.setOnClickListener {
                onLanguageSelected.onKoreanSelected()
            }
            binding.enlgishClickLayout.setOnClickListener {
                onLanguageSelected.onEnglishSelected()
            }

            builder.setView(binding.root)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }


    interface OnLanguageSelected{
        fun onKoreanSelected()
        fun onEnglishSelected()
    }

    fun setListener(onLanguageSelected: OnLanguageSelected){
        this.onLanguageSelected = onLanguageSelected
    }

}