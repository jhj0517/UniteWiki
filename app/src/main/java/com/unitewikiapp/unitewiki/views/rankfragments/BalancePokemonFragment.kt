package com.unitewikiapp.unitewiki.views.rankfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.unitewikiapp.unitewiki.adapters.PokemonRankAdapter
import com.unitewikiapp.unitewiki.databinding.FragmentBalancePokemonBinding
import com.unitewikiapp.unitewiki.utils.Constants.BALANCE_RANKING
import com.unitewikiapp.unitewiki.viewmodels.PokemonRankViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class BalancePokemonFragment : Fragment() {

    private var _binding: FragmentBalancePokemonBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PokemonRankViewModel by viewModels()

    private var adapter: PokemonRankAdapter = PokemonRankAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentBalancePokemonBinding.inflate(inflater,container,false)
        binding.apply {
            balanceRecyclerview.adapter = adapter
            balanceRecyclerview.layoutManager = LinearLayoutManager(activity)
        }

        runBlocking {
            launch {
                subscribeUi()
            }.join()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeUi() {
        viewModel.fetchRanks(BALANCE_RANKING)
        viewModel.rankList.observe(viewLifecycleOwner){ res->
            adapter.submitList(res)
            binding.loadComplete = !(res.isNullOrEmpty())
        }
    }

}