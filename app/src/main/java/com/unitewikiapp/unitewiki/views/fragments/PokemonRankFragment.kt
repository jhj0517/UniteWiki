package com.unitewikiapp.unitewiki.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.unitewikiapp.unitewiki.adapters.PokemonRankAdapter
import com.unitewikiapp.unitewiki.databinding.FragmentPokemonRankBinding
import com.unitewikiapp.unitewiki.viewmodels.PokemonInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class PokemonRankFragment : Fragment() {

    private var _binding: FragmentPokemonRankBinding? = null
    private val binding get() = _binding!!
    private val viewModel:PokemonInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPokemonRankBinding.inflate(inflater,container,false)

        val adapter = PokemonRankAdapter()
        binding.apply {
            binding.rankRecyclerview.adapter = adapter
            rankRecyclerview.layoutManager = LinearLayoutManager(activity)
        }

        runBlocking {
            launch {
                subscribeUi(adapter, arguments!!.getString(RANKING_TYPE_ARG)!!)
            }.join()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeUi(adapter:PokemonRankAdapter, rankingType: String) {
        viewModel.infoSnapshot.observe(viewLifecycleOwner){
            binding.loadComplete = it != null
            if (binding.loadComplete) {
                val list = viewModel.getPokemonRankingInfo(rankingType)
                adapter.submitList(list)
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