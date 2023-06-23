package com.unitewikiapp.unitewiki.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.librarydevloperjo.kcs.KCS
import com.unitewikiapp.unitewiki.adapters.SearchListParentAdapter
import com.unitewikiapp.unitewiki.databinding.FragmentSearchListBinding
import com.unitewikiapp.unitewiki.datas.PokemonSearchData
import com.unitewikiapp.unitewiki.utils.LocaleStore
import com.unitewikiapp.unitewiki.viewmodels.PokemonSearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchListFragment : Fragment() {

    private var _binding:FragmentSearchListBinding? = null
    private val binding get() = _binding!!
    private val viewModel : PokemonSearchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSearchListBinding.inflate(inflater,container,false)
        subscribeUI()
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    private fun subscribeUI(){
        viewModel.getSearchData()
        val sortedlist:ArrayList<PokemonSearchData> = arrayListOf()
        val dataList: ArrayList<PokemonSearchData> = arrayListOf()
        viewModel.pokemondatalist.observe(viewLifecycleOwner) { result ->

            val country = LocaleStore(requireActivity()).findLocale()

            if(!result.isNullOrEmpty()){
                dataList.addAll(result)

                val groupBy = dataList.groupBy {
                    when (country) {
                        "" -> {
                            // KCS is a utility class that specializes in Korean initial sound search.
                            KCS.getCho(it.pokemon_name!!.substring(0,1))
                        }
                        else -> {
                            it.pokemon_name!!.substring(0, 1)
                        }
                    }
                }

                val keyList = groupBy.keys.toList()
                val valueList_ = groupBy.values.toList()
                val valueList = ArrayList(valueList_)

                for (i in 0 until keyList.size) {
                    if (sortedlist.size < keyList.size) {
                        sortedlist.add(i,
                            PokemonSearchData(letter = keyList[i],
                                childList = ArrayList(valueList[i])))
                    }
                }
                val adapter = SearchListParentAdapter(requireActivity())
                adapter.submitList(sortedlist)
                binding.recyclerview.adapter = adapter
                binding.recyclerview.layoutManager = LinearLayoutManager(activity)
            }
        }
    }
}