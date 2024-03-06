package com.unitewikiapp.unitewiki.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.unitewikiapp.unitewiki.R
import com.unitewikiapp.unitewiki.adapters.PokemonReviewsAdapter
import com.unitewikiapp.unitewiki.databinding.FragmentPokemonReviewsBinding
import com.unitewikiapp.unitewiki.datas.PokemonReviewsData
import com.unitewikiapp.unitewiki.utils.LocaleStore
import com.unitewikiapp.unitewiki.utils.ReviewPopup
import com.unitewikiapp.unitewiki.viewmodels.LoginViewModel
import com.unitewikiapp.unitewiki.viewmodels.PokemonInfoViewModel
import com.unitewikiapp.unitewiki.viewmodels.PokemonReviewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class PokemonReviewsFragment : Fragment(), PokemonReviewsAdapter.ClickCallback, ReviewPopup.ClickCallback{

    @Inject
    lateinit var localeStore: LocaleStore

    private lateinit var pokemonName:String
    private val reviewViewModel:PokemonReviewsViewModel by activityViewModels()
    private val infoViewModel:PokemonInfoViewModel by activityViewModels()
    private val authViewModel:LoginViewModel by activityViewModels()
    private var _binding:FragmentPokemonReviewsBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pokemonName =arguments?.getString("pokemonname")?:""
        infoViewModel.setCurrentPokemon(pokemonName)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
        ): View? {
        _binding = FragmentPokemonReviewsBinding.inflate(inflater,container,false)

        val reviewsAdapter = PokemonReviewsAdapter(false,this, localeStore, authViewModel.currentUser.value)
        val myReviewAdapter = PokemonReviewsAdapter(true,this, localeStore, authViewModel.currentUser.value)
        runBlocking {

        binding.apply {
            isEmpty=true
            pokemonNameIntoolbar.text = pokemonName
            reviewsRecyclerview.adapter = reviewsAdapter
            reviewsRecyclerview.layoutManager = LinearLayoutManager(activity)

            myreview.adapter = myReviewAdapter
            myreview.layoutManager = LinearLayoutManager(activity)

            btnSetting.setOnClickListener {
                val direction = PokemonReviewsFragmentDirections.actionPokemonReviewsFragmentToSettingFragment()
                findNavController().navigate(direction)
            }

            writingButtonEmptyRoot.setOnClickListener {
                navigateAfterLoginCheck()
            }

            writingButtonRoot.setOnClickListener {
                navigateAfterLoginCheck()
            }

            navigateBackbtn.setOnClickListener {
                findNavController().popBackStack()
            }

            sortingButton.setOnClickListener {
                val pop = PopupMenu(requireActivity(),binding.sortingButton)
                pop.inflate(R.menu.popup_menu)

                pop.setOnMenuItemClickListener { item->
                    when (item.itemId){
                        R.id.sortbylikes-> {
                            val sorted = reviewViewModel.getLikeSortedReview(infoViewModel.currentPokemon.value!!.pokemon_name)
                            reviewsAdapter.submitList(sorted.filter { authViewModel.currentUser.value?.uid != it.uid })
                        }
                        R.id.sortbyrecent->{
                            val sorted = reviewViewModel.getTimeSortedReview(infoViewModel.currentPokemon.value!!.pokemon_name)
                            reviewsAdapter.submitList(sorted.filter { authViewModel.currentUser.value?.uid != it.uid })
                        }
                    }
                    false
                }
                pop.show()
            }
        }

        }
        subscribeUI(reviewsAdapter,myReviewAdapter)
        return binding.root
    }

    private fun subscribeUI(adapter:PokemonReviewsAdapter,myReviewAdapter: PokemonReviewsAdapter) {
        binding.apply {
            isEmpty = reviewViewModel.currentReviews.value.isNullOrEmpty()

            reviewViewModel.reviewSnapshot.observe(viewLifecycleOwner){
                reviewViewModel.setCurrentReviews()
                val reviews = reviewViewModel.getLikeSortedReview(infoViewModel.currentPokemon.value!!.pokemon_name)
                val (myReview, others) = reviews.partition {
                    it.uid == authViewModel.currentUser.value?.uid
                }
                isMyReviewExist = myReview.isNotEmpty()
                adapter.submitList(others)
                myReviewAdapter.submitList(myReview)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private fun navigateAfterLoginCheck(){
        val user = authViewModel.currentUser.value
        if(user?.uid!=null){
            val direction = PokemonReviewsFragmentDirections.actionPokemonReviewsFragmentToReviewWritingFragment(pokemonName,false)
            findNavController().navigate(direction)
        } else {
            authViewModel.signIn(requireActivity())
        }
    }

}