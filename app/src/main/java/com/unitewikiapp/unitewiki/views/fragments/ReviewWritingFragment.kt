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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.unitewikiapp.unitewiki.R
import com.unitewikiapp.unitewiki.databinding.FragmentReviewWritingBinding
import com.unitewikiapp.unitewiki.datas.PokemonReviewsData
import com.unitewikiapp.unitewiki.viewmodels.LoginViewModel
import com.unitewikiapp.unitewiki.viewmodels.PokemonInfoViewModel
import com.unitewikiapp.unitewiki.viewmodels.PokemonReviewsViewModel
import com.unitewikiapp.unitewiki.views.dialogfragments.WritingDialogFragment
import dagger.hilt.android.AndroidEntryPoint

const val MAX_REVIEW_LENGTH = 1000

@AndroidEntryPoint
class ReviewWritingFragment : Fragment()
    , WritingDialogFragment.OnDialogButtonClick{

    private lateinit var pokemonname:String
    private var isEditing:Boolean? = null
    private lateinit var callback: OnBackPressedCallback
    private val dialog = WritingDialogFragment()
    private val infoViewModel :PokemonInfoViewModel by activityViewModels()
    private val reviewViewModel: PokemonReviewsViewModel by activityViewModels()
    private val authViewModel: LoginViewModel by activityViewModels()

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
            infoVm = infoViewModel
            reviewVm = reviewViewModel
            lifecycleOwner = viewLifecycleOwner
            addETListeners(reviewWriting, reviewlenIndicator, buttonCommit)

            skill1.setOnClickListener { skill1Selected = true }
            skill2.setOnClickListener { skill1Selected = false }
            skill3.setOnClickListener { skill3Selected = true }
            skill4.setOnClickListener { skill3Selected = false }

            buttonCommit.setOnClickListener {
                saveDraft()
                findNavController().popBackStack()
                reviewViewModel.addReview(reviewViewModel.draft.value!!)
                reviewViewModel.cleanDraft()
            }
            navigateBackbtn.setOnClickListener {
                dialog.show(requireActivity().supportFragmentManager,"dialog")
                reviewViewModel.cleanDraft()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
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

    private fun saveDraft(){
        binding.apply {
            val L_Skill = if (skill1Selected) 1 else 2
            val R_Skill = if (skill3Selected) 1 else 2
            reviewViewModel.setDraft(
                PokemonReviewsData(
                    writing = reviewWriting.text.toString(),
                    time = System.currentTimeMillis(),
                    selectedSkills = "${L_Skill},${R_Skill}",
                    rating = ratingBar.rating.toInt(),
                    pokemon = infoViewModel.currentPokemon.value!!.pokemon_name,
                    edited = isEditing!!,
                    uid = authViewModel.currentUser.value!!.uid,
                    userName = authViewModel.currentUser.value!!.displayName,
                    reported = reviewViewModel.draft.value?.reported?: hashMapOf(),
                    likes = reviewViewModel.draft.value?.likes ?: hashMapOf()
                )
            )
        }
    }

    override fun onOKClick() {
        findNavController().popBackStack()
        dialog.dismiss()
    }

    override fun onNOClick() {
        dialog.dismiss()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialog.setListener(this)
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