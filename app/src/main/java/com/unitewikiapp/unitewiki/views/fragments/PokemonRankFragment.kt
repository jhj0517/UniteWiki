package com.unitewikiapp.unitewiki.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MediatorLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.unitewikiapp.unitewiki.adapters.PokemonRankAdapter
import com.unitewikiapp.unitewiki.databinding.FragmentPokemonRankBinding
import com.unitewikiapp.unitewiki.viewmodels.PokemonInfoViewModel
import com.unitewikiapp.unitewiki.viewmodels.PokemonReviewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PokemonRankFragment : Fragment() {

    private var _binding: FragmentPokemonRankBinding? = null
    private val binding get() = _binding!!
    private val infoViewModel:PokemonInfoViewModel by activityViewModels()
    private val reviewViewModel:PokemonReviewsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPokemonRankBinding.inflate(inflater,container,false)

        val adapter = PokemonRankAdapter()
        binding.apply {
            rankRecyclerview.adapter = adapter
            rankRecyclerview.layoutManager = LinearLayoutManager(activity)
        }

        subscribeUi(adapter, arguments!!.getString(RANKING_TYPE_ARG)!!)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeUi(adapter:PokemonRankAdapter, rankingType: String) {
        val infos = infoViewModel.infoSnapshot
        val reviews = reviewViewModel.reviewSnapshot

        val combined = MediatorLiveData<Pair<DataSnapshot?, DataSnapshot?>>()
        combined.addSource(infos){ newValue -> combined.value = Pair(newValue, reviews.value) }
        combined.addSource(reviews){ newValue -> combined.value = Pair(infos.value, newValue) }

        combined.observe(viewLifecycleOwner){ (infoSnap, reviewSnap) ->
            if (reviewSnap != null){
                reviewViewModel.setReviews()
            }
            if (reviewSnap != null && infoSnap != null){
                binding.loadComplete = true
                val unsorted = infoViewModel.getRankDataByType(rankingType)
                val sorted = reviewViewModel.sortByScore(unsorted)
                adapter.submitList(sorted)
            }
        }
    }

    companion object {
        const val RANKING_TYPE_ARG = "ranking_type_arg"

        @JvmStatic
        fun newInstance(rankingType: String) =
            PokemonRankFragment().apply {
                arguments = Bundle().apply {
                    putString(RANKING_TYPE_ARG, rankingType)
                }
            }
    }

}