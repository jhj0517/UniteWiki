package com.unitewikiapp.unitewiki.views.dialogfragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.unitewikiapp.unitewiki.databinding.DialogWritingcancelBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WritingDialogFragment: DialogFragment(){

    lateinit var onButtonClick: OnDialogButtonClick

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val binding =  DialogWritingcancelBinding.inflate(LayoutInflater.from(context))

            binding.YES.setOnClickListener {
                 onButtonClick.onOKClick()
            }
            binding.NO.setOnClickListener {
                onButtonClick.onNOClick()
            }
            builder.setView(binding.root)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    interface OnDialogButtonClick{
        fun onOKClick()
        fun onNOClick()
    }

    fun setListener(onDialogButtonClick: OnDialogButtonClick){
        this.onButtonClick = onDialogButtonClick
    }

}