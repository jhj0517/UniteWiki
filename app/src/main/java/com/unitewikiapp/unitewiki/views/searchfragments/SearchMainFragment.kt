package com.unitewikiapp.unitewiki.views.searchfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.unitewikiapp.unitewiki.adapters.SearchViewPagerAdapter
import com.unitewikiapp.unitewiki.databinding.FragmentSearchMainBinding
import com.unitewikiapp.unitewiki.fragments.SearchFragment
import com.unitewikiapp.unitewiki.fragments.SearchListFragment
import com.unitewikiapp.unitewiki.viewmodels.QueryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchMainFragment : Fragment() {

    private var _binding: FragmentSearchMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel : QueryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSearchMainBinding.inflate(inflater, container, false)

        val pagerAdapter = SearchViewPagerAdapter(this)

        val searchListFragment = SearchListFragment()
        pagerAdapter.addFragment(searchListFragment)

        val searchFragment  = SearchFragment()
        pagerAdapter.addFragment(searchFragment)

        binding.apply {
            viewpager.adapter = pagerAdapter
            viewpager.setCurrentItem(0, false)
            viewpager.isUserInputEnabled = false

            searchbar.addTextChangedListener { str ->
                viewModel.setQuery(str.toString())

                when (str.toString()) {
                    "" -> {
                        viewpager.setCurrentItem(0, false)
                    }
                    else -> {
                        viewpager.setCurrentItem(1, false)
                    }
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}