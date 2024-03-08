package com.unitewikiapp.unitewiki.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.unitewikiapp.unitewiki.adapters.SearchListParentAdapter
import com.unitewikiapp.unitewiki.databinding.FragmentSearchListBinding
import com.unitewikiapp.unitewiki.utils.LocaleStore
import com.unitewikiapp.unitewiki.viewmodels.PokemonInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchListFragment : Fragment() {

    private var _binding:FragmentSearchListBinding? = null
    private val binding get() = _binding!!
    private val viewModel : PokemonInfoViewModel by activityViewModels()

    @Inject
    lateinit var localeStore: LocaleStore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSearchListBinding.inflate(inflater,container,false)
        val adapter = SearchListParentAdapter(requireActivity(), localeStore)
        subscribeUI(adapter)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun subscribeUI(adapter: SearchListParentAdapter){
        viewModel.infoSnapshot.observe(viewLifecycleOwner) {
            if (it != null){
                val list = viewModel.getListByAlphabet()
                adapter.submitList(list)
                binding.recyclerview.adapter = adapter
                binding.recyclerview.layoutManager = LinearLayoutManager(activity)
            }
        }
    }

}