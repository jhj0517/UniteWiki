package com.unitewikiapp.unitewiki.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.unitewikiapp.unitewiki.R
import com.unitewikiapp.unitewiki.adapters.PokemonReviewsAdapter
import com.unitewikiapp.unitewiki.databinding.FragmentPokemonReviewsBinding
import com.unitewikiapp.unitewiki.datas.PokemonReviewsData
import com.unitewikiapp.unitewiki.utils.Constants
import com.unitewikiapp.unitewiki.utils.ShortPopupWindow
import com.unitewikiapp.unitewiki.viewmodels.LoginViewModel
import com.unitewikiapp.unitewiki.viewmodels.PokemonReviewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class PokemonReviewsFragment : Fragment(), PokemonReviewsAdapter.ClickCallback{

    private lateinit var pokemonName:String
    private val reviewsAdapter = PokemonReviewsAdapter(false,this)
    private val myReviewAdapter = PokemonReviewsAdapter(true,this)
    private val viewModel:PokemonReviewsViewModel by viewModels()
    private val loginViewModel:LoginViewModel by activityViewModels()
    private var _binding:FragmentPokemonReviewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pokemonName =arguments?.getString("pokemonname")?:""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
        ): View? {
        _binding = FragmentPokemonReviewsBinding.inflate(inflater,container,false)

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
                        R.id.sortbylikes-> {removeReviewsListener()
                                            addReviewsListeners(Constants.QUERY_LIKES_NUMBER)}
                        R.id.sortbyrecent->{ removeReviewsListener()
                                            addReviewsListeners(Constants.QUERY_TIME)}
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

    private fun addReviewsListeners(query:String){
        viewModel.addReviewValueListener(pokemonName,query)
    }

    private fun removeReviewsListener(){
        viewModel.removeReviewValueListener(pokemonName,Constants.QUERY_TIME)
    }

    private fun subscribeUI(adapter:PokemonReviewsAdapter,myadapter: PokemonReviewsAdapter) {
        addReviewsListeners(Constants.QUERY_LIKES_NUMBER)
        runBlocking {
            launch {
                viewModel.reviewData.observe(viewLifecycleOwner) {
                    binding.isEmpty = it.isNullOrEmpty()
                    adapter.submitList(it)
                }
            }.join()
            launch {
                viewModel.myReviewData.observe(viewLifecycleOwner) {
                    binding.doIwritereivew = !(it.isNullOrEmpty())
                    myadapter.submitList(it)
                }
            }.join()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClickLikeButton(position:Int, itemData:PokemonReviewsData?, likeView:ImageView) {
        val user = loginViewModel.getUser().value
        if(user != null){
            viewModel.onLikeClicked(pokemonName,user.uid,itemData!!.uid!!)
        } else{
            loginViewModel.signIn(requireActivity())
        }
    }

    override fun onClickPopupEditMenu(position: Int, itemData: PokemonReviewsData?, anchor: ImageView) {
        val user = loginViewModel.getUser().value
        var pop = PopupMenu(context,anchor)
        pop.inflate(R.menu.edit_menu)

        pop.setOnMenuItemClickListener { item->
            when (item.itemId){
                R.id.edit-> {
                    if(user!= null){
                        val direction = PokemonReviewsFragmentDirections.actionPokemonReviewsFragmentToReviewWritingFragment(pokemonName,true)
                        findNavController().navigate(direction)
                    }else{
                        Toast.makeText(context, R.string.needtologin, Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.delete ->{
                    if(user!=null){
                        viewModel.removeMyReview(pokemonName,user.uid!!)
                        val loginPopup = ShortPopupWindow(context)
                        loginPopup.setText(context?.resources?.getString(R.string.deletereivewsuccess))
                        if(!loginPopup.isTooltipShown) {
                            loginPopup.showToolTip(binding.btnSetting)
                        }
                    }else{
                        Toast.makeText(context, R.string.needtologin, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            false
        }
        pop.show()
    }

    override fun onClickPopupReportMenu(position: Int, itemData:PokemonReviewsData?, anchor:ImageView) {
        val user = loginViewModel.getUser().value
        val pop = PopupMenu(context,anchor)
        pop.inflate(R.menu.report_menu)
        pop.setOnMenuItemClickListener { item->
            when (item.itemId){
                R.id.report-> {
                    if(user != null){
                        viewModel.setReportTrue(pokemonName,user.uid,itemData!!.uid!!)
                        val reportPopup = ShortPopupWindow(context)
                        reportPopup.setText(context?.resources?.getString(R.string.reviewreported))
                        if(!reportPopup.isTooltipShown){
                            reportPopup.showToolTip(binding.btnSetting)
                        }
                    }else{
                        val loginPopup = ShortPopupWindow(context)
                        loginPopup.setText(context?.resources?.getString(R.string.needlogingtodothis))
                        if(!loginPopup.isTooltipShown){
                            loginPopup.showToolTip(binding.btnSetting)
                        }
                    }
                }
            }
            false
        }
        pop.show()
    }

    private fun navigateAfterLoginCheck(){
        val user = loginViewModel.getUser().value
        if(user?.uid!=null){
            val direction = PokemonReviewsFragmentDirections.actionPokemonReviewsFragmentToReviewWritingFragment(pokemonName,false)
            findNavController().navigate(direction)
        } else {
            loginViewModel.signIn(requireActivity())
        }
    }

    fun signOut() {
        loginViewModel.getAuthUI().signOut(requireActivity())
    }

}