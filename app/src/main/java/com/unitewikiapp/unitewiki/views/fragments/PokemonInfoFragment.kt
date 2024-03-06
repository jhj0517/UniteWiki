package com.unitewikiapp.unitewiki.views.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.unitewikiapp.unitewiki.R
import com.unitewikiapp.unitewiki.adapters.PokemonReviewsAdapter
import com.unitewikiapp.unitewiki.databinding.FragmentPokemonInfoBinding
import com.unitewikiapp.unitewiki.datas.LocaleField
import com.unitewikiapp.unitewiki.datas.PokemonReviewsData
import com.unitewikiapp.unitewiki.datas.localized
import com.unitewikiapp.unitewiki.utils.LocaleStore
import com.unitewikiapp.unitewiki.utils.ReviewPopup
import com.unitewikiapp.unitewiki.utils.TooltipWindow
import com.unitewikiapp.unitewiki.viewmodels.LoginViewModel
import com.unitewikiapp.unitewiki.viewmodels.PokemonInfoViewModel
import com.unitewikiapp.unitewiki.viewmodels.PokemonReviewsViewModel
import com.unitewikiapp.unitewiki.views.fragments.PokemonInfoFragment.ToolCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_pokemon_info.*
import javax.inject.Inject

@AndroidEntryPoint
class PokemonInfoFragment : Fragment(),
    PokemonReviewsAdapter.ClickCallback,
    ReviewPopup.ClickCallback {

    private lateinit var pokemonName:String
    private val infoViewModel:PokemonInfoViewModel by activityViewModels()
    private val reviewViewModel:PokemonReviewsViewModel by activityViewModels()
    private val authViewModel:LoginViewModel by activityViewModels()
    private lateinit var backCallBack: OnBackPressedCallback
    private lateinit var tooltip:TooltipWindow
    @Inject
    lateinit var localeStore:LocaleStore

    private var _binding: FragmentPokemonInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pokemonName = arguments?.getString("pokemonname") ?: ""
        infoViewModel.setCurrentPokemon(pokemonName)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPokemonInfoBinding.inflate(inflater,container,false)
        val adapter = PokemonReviewsAdapter(false, this, localeStore, authViewModel.currentUser.value)
        binding.apply {
            infoVm = infoViewModel
            lifecycleOwner = viewLifecycleOwner

            simpleReviewRecyclerview.adapter = adapter
            simpleReviewRecyclerview.layoutManager = LinearLayoutManager(activity)

            simpleReviewRecyclerview.setOnClickListener {
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

        setToolTip(binding)
        showAppBar()
        subscribeUI(adapter)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun interface ToolCallback {
        fun showTooltip(skillName:LocaleField, skillCoolTime:String, skillDescription:LocaleField, view:View)
    }

    private fun subscribeUI(adapter: PokemonReviewsAdapter){
        infoViewModel.currentPokemon.observe(viewLifecycleOwner){ pokemon ->
            binding.loadComplete = pokemon != null
        }

        reviewViewModel.reviewSnapshot.observe(viewLifecycleOwner){ review ->
            reviewViewModel.setCurrentReviews()
            binding.apply {
                val name = infoViewModel.currentPokemon.value!!.pokemon_name
                reviewCount = reviewViewModel.getReviewCount(name)
                averageScore = reviewViewModel.getAverageScore(name)
                val skillSelections = reviewViewModel.getSkillPreference(name)
                lLeftArrow = skillSelections[0] >= skillSelections[1]
                rLeftArrow = skillSelections[2] >= skillSelections[3]
                lSkillPreference = reviewViewModel.calculatePreference(skillSelections[0], skillSelections[1])
                rSkillPreference = reviewViewModel.calculatePreference(skillSelections[2], skillSelections[3])

                var simpleReviews = reviewViewModel.getLikeSortedReview(name).take(3)
                if(authViewModel.currentUser.value!=null) {
                    simpleReviews = reviewViewModel.filterReportedReview(
                        simpleReviews,
                        authViewModel.currentUser.value!!
                    )
                }
                adapter.submitList(simpleReviews)
            }
        }
    }

    private fun navigateWithDirection(direction: NavDirections){
        findNavController().navigate(direction)
    }

    private fun setToolTip(binding: FragmentPokemonInfoBinding){
        binding.apply {
            tooltip = TooltipWindow(context)
            toolcallback = ToolCallback { skillName, skillCoolTime, skillDescription,view ->
                tooltip.setText(
                    skillName.localized(localeStore.locale!!),
                    skillCoolTime,
                    skillDescription.localized(localeStore.locale!!)
                )
                if (!tooltip.isTooltipShown()){
                    tooltip.showToolTip(view)
                    tooltip.showChecking(view)
                }
            }
        }
    }

    private fun showAppBar() {
        var isAppBarShown = false
        binding.pokemonIcIntoolbar.isVisible = isAppBarShown
        binding.pokemonCircleIntoolbar.isVisible = isAppBarShown

        binding.infoNestedscrollview.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->

                val shouldShowToolbar = scrollY > (profile.height+10)

                if (isAppBarShown != shouldShowToolbar) {
                    isAppBarShown = shouldShowToolbar

                    binding.pokemonIcIntoolbar.isVisible = shouldShowToolbar
                    binding.pokemonCircleIntoolbar.isVisible = shouldShowToolbar
                }
            }
        )
    }

    override fun onClickLikeButton(
        position: Int,
        itemData: PokemonReviewsData?,
        likeView: ImageView
    ) {
        val user = authViewModel.currentUser.value
        if(user == null){
            authViewModel.signIn(requireActivity())
            return
        }
        reviewViewModel.updateLike(itemData!!, user)
    }

    override fun onClickPopupMenu(
        position: Int,
        itemData: PokemonReviewsData?,
        anchor: ImageView
    ) {
        val user = authViewModel.currentUser.value
        if (user==null){
            authViewModel.signIn(requireActivity())
            return
        }
        val menu:Int = if (itemData!!.uid==user.uid){
            R.menu.edit_menu
        } else {
            R.menu.report_menu
        }
        ReviewPopup.showMenu(
            context = requireActivity(),
            anchor = anchor,
            menuRes = menu,
            review = itemData,
            position = position,
            clickCallback = this
        )
    }

    override fun onPopupMenuItemClick(itemId: Int, position:Int, itemData: PokemonReviewsData?, anchor: View){
        when (itemId) {
            R.id.edit -> {
                val direction = PokemonReviewsFragmentDirections.actionPokemonReviewsFragmentToReviewWritingFragment(pokemonName,true)
                findNavController().navigate(direction)
            }
            R.id.delete -> {
                reviewViewModel.removeReview(itemData!!)
            }
            R.id.report -> {
                reviewViewModel.reportReview(itemData!!, authViewModel.currentUser.value!!)
            }
        }
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