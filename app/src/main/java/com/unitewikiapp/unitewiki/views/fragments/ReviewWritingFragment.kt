package com.unitewikiapp.unitewiki.views.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.unitewikiapp.unitewiki.R
import com.unitewikiapp.unitewiki.databinding.FragmentReviewWritingBinding
import com.unitewikiapp.unitewiki.datas.PokemonReviewWritingData
import com.unitewikiapp.unitewiki.viewmodels.LoginViewModel
import com.unitewikiapp.unitewiki.viewmodels.PokemonReviewWrtingViewModel
import com.unitewikiapp.unitewiki.views.dialogfragments.SelectsSkillsDialogFragment
import com.unitewikiapp.unitewiki.views.dialogfragments.WritingDialogFragment
import dagger.hilt.android.AndroidEntryPoint

const val MAX_REVIEW_LENGTH = 1000

@AndroidEntryPoint
class ReviewWritingFragment : Fragment()
    , WritingDialogFragment.OnDialogButtonClick
    , SelectsSkillsDialogFragment.OnDialogButtonClick{

    private lateinit var pokemonname:String
    private var isEditing:Boolean? = null
    private lateinit var callback: OnBackPressedCallback
    private val dialog = WritingDialogFragment()
    private val selectDialog = SelectsSkillsDialogFragment()
    private val viewmodel :PokemonReviewWrtingViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    private var _binding:FragmentReviewWritingBinding?=null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pokemonname =arguments?.getString("pokemonname")?:""
        isEditing = arguments?.getBoolean("IsEditing")?:false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentReviewWritingBinding.inflate(inflater,container,false)
        binding.apply {
            viewModel = viewmodel
            lifecycleOwner = viewLifecycleOwner
            addETListeners(reviewWriting,reviewlenIndicator,buttonCommit)
            addSkillSelectListeners(this)
            buttonCommit.setOnClickListener {
                if(viewmodel.firstClickState.value==null || viewmodel.secondClickState.value==null){
                    selectDialog.show(requireActivity().supportFragmentManager,"dialog")
                }else{
                    val user = loginViewModel.currentUser
                    val data = PokemonReviewWritingData(
                        pokemon=pokemonname,
                        writing = reviewWriting.text.toString(),
                        rating = ratingBar.rating.toInt(),
                        firstSkillSetRate = if(viewmodel.firstClickState.value == true) 0 else 1,
                        secondSkillSetRate = if(viewmodel.secondClickState.value == true) 0 else 1,
                        userName = user.value!!.displayName!!,
                        uid= user.value!!.uid
                    )
                    viewmodel.commitPosting(pokemonname,user.value!!.uid,data)

                    val direction = ReviewWritingFragmentDirections.actionReviewWritingFragmentToPokemonReviewsFragment(pokemonname)
                    findNavController().navigate(direction)
                }
            }
            navigateBackbtn.setOnClickListener {
                dialog.show(requireActivity().supportFragmentManager,"dialog")
            }
        }

        subscribeUI(pokemonname)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun subscribeUI(pokemonName: String) {
        viewmodel.fetchInfo(pokemonName)
        when (isEditing) {
            true -> {
                val user = loginViewModel.currentUser.value
                viewmodel.fetchMyReviewText(pokemonName,user!!.uid)
                viewmodel.myReview.observe(viewLifecycleOwner){
                    binding.reviewWriting.setText(it)
                }
            }
            else -> {}
        }
    }

    private fun addSkillSelectListeners(binding:FragmentReviewWritingBinding){
        // This will implement changes to the UI through the BindingAdapter class and the fragment_review_writing.xml.
        binding.apply {
            skill1.setOnClickListener { viewmodel.firstClickState.value = true }
            skill2.setOnClickListener { viewmodel.firstClickState.value= false }
            skill3.setOnClickListener { viewmodel.secondClickState.value = true }
            skill4.setOnClickListener { viewmodel.secondClickState.value = false }
        }
    }

    private fun addETListeners(et:EditText,tv: TextView,tvCommit:TextView){
        // different border color by focus and character limit for content.
        et.setSelection(et.length())
        et.addTextChangedListener {
            val length = et.text.length
            tv.text = "${length}/$MAX_REVIEW_LENGTH"

            if(length>= MAX_REVIEW_LENGTH){
                et.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.square_border_bold_red)
            }else if(et.hasFocus() && 0<length  && length< MAX_REVIEW_LENGTH){
                tvCommit.setTextColor(ContextCompat.getColor(requireActivity(),R.color.supportorange))
                tvCommit.isEnabled = true
                et.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.square_border_bold)
            }else if(length<=0){
                tvCommit.setTextColor(ContextCompat.getColor(requireActivity(),R.color.weakgrey))
                tvCommit.isEnabled = false
            }
        }

        et.setOnFocusChangeListener { v, hasFocus ->
            when (hasFocus) {
                true -> {
                    et.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.square_border_bold)
                }
                false -> {
                    et.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.square_border)
                }
            }
        }
    }

    override fun onOKClick() {
        val direction = ReviewWritingFragmentDirections.actionReviewWritingFragmentToPokemonReviewsFragment(pokemonname)
        findNavController().navigate(direction)
        dialog.dismiss()
    }

    override fun onNOClick() {
        dialog.dismiss()
    }

    override fun onOKlClick() {
        selectDialog.dismiss()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialog.setListener(this)
        selectDialog.setListener(this)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                dialog.show(requireActivity().supportFragmentManager,"dialog")
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}