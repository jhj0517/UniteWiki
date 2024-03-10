package com.unitewikiapp.unitewiki.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.unitewikiapp.unitewiki.adapters.PokemonSearchAdapter
import com.unitewikiapp.unitewiki.databinding.FragmentSearchBinding
import com.unitewikiapp.unitewiki.utils.LocaleStore
import com.unitewikiapp.unitewiki.viewmodels.PokemonInfoViewModel
import com.unitewikiapp.unitewiki.viewmodels.QueryViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(){

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val queryViewModel : QueryViewModel by activityViewModels()
    private val infoViewModel : PokemonInfoViewModel by activityViewModels()
    @Inject
    lateinit var localeStore: LocaleStore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val adapter = PokemonSearchAdapter(localeStore)
        subscribeUI(adapter)

        binding.apply {
            serachRecyclerview.adapter = adapter
            serachRecyclerview.layoutManager = GridLayoutManager(activity, 5)
        }
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun subscribeUI(adapter:PokemonSearchAdapter){
        infoViewModel.infoSnapshot.observe(viewLifecycleOwner) {
            if (it!=null){
                binding.loaded = true
                val list = infoViewModel.getList()
                adapter.setData(list)
            }
        }

        queryViewModel.query.observe(viewLifecycleOwner){
            adapter.filter.filter(it)
        }
    }

}

