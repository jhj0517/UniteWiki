package com.unitewikiapp.unitewiki.views.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.unitewikiapp.unitewiki.adapters.PokemonReviewInfoAdapter
import com.unitewikiapp.unitewiki.databinding.FragmentPokemonInfoBinding
import com.unitewikiapp.unitewiki.utils.Constants
import com.unitewikiapp.unitewiki.utils.TooltipWindow
import com.unitewikiapp.unitewiki.viewmodels.PokemonInfoViewModel
import com.unitewikiapp.unitewiki.views.fragments.PokemonInfoFragment.ToolCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_pokemon_info.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class PokemonInfoFragment : Fragment() {

    private lateinit var pokemonName:String
    private val adapter = PokemonReviewInfoAdapter()
    private val infoViewModel:PokemonInfoViewModel by viewModels()
    private lateinit var backCallBack: OnBackPressedCallback
    private lateinit var tooltip:TooltipWindow

    private var _binding: FragmentPokemonInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pokemonName = arguments?.getString("pokemonname") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPokemonInfoBinding.inflate(inflater,container,false)
        showToolbar()

        binding.apply {
            viewModel = infoViewModel
            lifecycleOwner = viewLifecycleOwner
            isReviewZero = true

            context?.let{
                tooltip = TooltipWindow(it)
                toolcallback = ToolCallback { skillName, skillCoolTime, skillDescription,view ->
                    tooltip.setText(skillName,skillCoolTime,skillDescription)
                    if (!tooltip.isTooltipShown()){
                        tooltip.showToolTip(view)
                        tooltip.showChecking(view)
                    }
                }
            }

            threeitemRecyclerview.adapter = adapter
            threeitemRecyclerview.layoutManager = LinearLayoutManager(activity)
            threeitemRecyclerview.setOnClickListener {
                val direction = PokemonInfoFragmentDirections.actionPokemonInfoFragmentToPokemonReviewsFragment(
                    this@PokemonInfoFragment.pokemonName
                )
                navigateWithDirection(direction)
            }

            navigateBackbtn.setOnClickListener {
                findNavController().popBackStack()
            }
            btnSetting.setOnClickListener {
                val direction = PokemonInfoFragmentDirections.actionPokemonInfoFragmentToSettingFragment()
                navigateWithDirection(direction)
            }
            reviewNavigationButton.setOnClickListener {
                val direction = PokemonInfoFragmentDirections.actionPokemonInfoFragmentToPokemonReviewsFragment(
                    this@PokemonInfoFragment.pokemonName
                )
                navigateWithDirection(direction)
            }
            NoReviewIndicator.setOnClickListener {
                val direction = PokemonInfoFragmentDirections.actionPokemonInfoFragmentToPokemonReviewsFragment(
                    this@PokemonInfoFragment.pokemonName
                )
                navigateWithDirection(direction)
            }
        }
        subscribeUI(pokemonName)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeUI(pokemonName: String){

        runBlocking {
            launch {
                infoViewModel.fetchPokemonInfo(pokemonName)
                infoViewModel.pokemonInfoData.observe(viewLifecycleOwner){ infoData->
                    binding.apply {
                        fullLoaded = !(infoData.ic_pokemon.isNullOrEmpty())
                    }
                }
                infoViewModel.firstRateSkillSet.observe(viewLifecycleOwner){
                    binding.skill1selected = it>=50 && it>0
                }
                infoViewModel.secondRateSkillSet.observe(viewLifecycleOwner){
                    binding.skill3selected = it>=50 && it>0
                }
            }.join()

            launch {
                infoViewModel.addReviewListener(pokemonName,Constants.QUERY_LIKES_NUMBER)
                infoViewModel.fetchRating(pokemonName)
                infoViewModel.pokemonReviewData.observe(viewLifecycleOwner){ reviewData->
                    binding.isReviewZero = reviewData.isNullOrEmpty()
                    adapter.submitList(reviewData)
                }

                infoViewModel.isCalculationComplete.observe(viewLifecycleOwner){
                    binding.iscalculateEnd = it
                }
            }.join()
        }
    }

    private fun navigateWithDirection(direction: NavDirections){
        findNavController().navigate(direction)
    }

    fun interface ToolCallback {
        fun showTooltip(skillName:String, skillCoolTime:String, skillDescription:String, view:View)
    }

    private fun showToolbar() {
        var isToolbarShown = false
        binding.pokemonIcIntoolbar.isVisible = isToolbarShown
        binding.pokemonCircleIntoolbar.isVisible = isToolbarShown

        binding.infoNestedscrollview.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->

                val shouldShowToolbar = scrollY > (profile.height+10)

                if (isToolbarShown != shouldShowToolbar) {
                    isToolbarShown = shouldShowToolbar

                    binding.pokemonIcIntoolbar.isVisible = shouldShowToolbar
                    binding.pokemonCircleIntoolbar.isVisible = shouldShowToolbar
                }
            }
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backCallBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (tooltip.isTooltipShown()){
                    tooltip.dismissTooltip()
                }
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backCallBack)
    }

    override fun onDetach() {
        super.onDetach()
        backCallBack.remove()
    }

}