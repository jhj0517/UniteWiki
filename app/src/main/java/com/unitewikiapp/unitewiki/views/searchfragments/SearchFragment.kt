package com.unitewikiapp.unitewiki.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.unitewikiapp.unitewiki.adapters.PokemonSearchAdapter
import com.unitewikiapp.unitewiki.databinding.FragmentSearchBinding
import com.unitewikiapp.unitewiki.viewmodels.PokemonSearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(){

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel : PokemonSearchViewModel by activityViewModels()
    private var pokemonadapter: PokemonSearchAdapter = PokemonSearchAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        subscribe_UI()

        binding.apply {
            serachRecyclerview.adapter = pokemonadapter
            serachRecyclerview.layoutManager = GridLayoutManager(activity, 5)

            viewModel.getQuery()!!.observe(viewLifecycleOwner,object :Observer<String>{
                override fun onChanged(str: String?) {
                    pokemonadapter.filter.filter(str)
                }
            })
        }
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    private fun subscribe_UI(){
        viewModel.getSearchData()
        viewModel.pokemondatalist.observe(viewLifecycleOwner) { result ->
            binding.loaded = !(result.isNullOrEmpty())
            pokemonadapter.setData(result)
        }
    }

}

