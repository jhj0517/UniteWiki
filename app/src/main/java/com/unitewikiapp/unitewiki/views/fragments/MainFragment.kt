package com.unitewikiapp.unitewiki.views.fragments

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.unitewikiapp.unitewiki.R
import com.unitewikiapp.unitewiki.adapters.*
import com.unitewikiapp.unitewiki.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_tab_view.view.*

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding:FragmentMainBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater,container,false)
        binding.apply {
            btnSearch.setOnClickListener {
                val direction = MainFragmentDirections.actionMainFragmentToSearchMainFragment()
                it.findNavController().navigate(direction)
            }
            btnSetting.setOnClickListener {
                val direction = MainFragmentDirections.actionMainFragmentToSettingFragment()
                it.findNavController().navigate(direction)
            }

            viewpager.adapter = ViewPagerAdapter(this@MainFragment)

            TabLayoutMediator(tablayout,viewpager) { tab, position ->
                tab.customView = getTabview(position)
            }.attach()

            addTabListener(tablayout)
        }
        return binding.root
    }

    private fun addTabListener(tablt:TabLayout){

        tablt.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab!!.customView!!.title.setTypeface(ResourcesCompat.getFont(requireContext(),R.font.aritadotum),Typeface.BOLD)

                // Showing different tab colors based on selection.
                when(tab?.position){
                    ATTACK_INDEX -> binding.tablayout.setSelectedTabIndicatorColor(ContextCompat.getColor(requireActivity(),R.color.attackred))
                    SPEED_INDEX -> binding.tablayout.setSelectedTabIndicatorColor(ContextCompat.getColor(requireActivity(),R.color.speedblue))
                    BALANCE_INDEX -> binding.tablayout.setSelectedTabIndicatorColor(ContextCompat.getColor(requireActivity(),R.color.balancepurple))
                    DEFENSE_INDEX -> binding.tablayout.setSelectedTabIndicatorColor(ContextCompat.getColor(requireActivity(),R.color.defensegreen))
                    SUPPORT_INDEX -> binding.tablayout.setSelectedTabIndicatorColor(ContextCompat.getColor(requireActivity(),R.color.supportorange))
                    else -> binding.tablayout.setSelectedTabIndicatorColor(ContextCompat.getColor(requireActivity(),R.color.attackred))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab!!.customView!!.title.setTypeface(ResourcesCompat.getFont(requireContext(),R.font.aritadotum),Typeface.NORMAL)
                when(tab.position){
                    //TO DO When Tab Unselected
                }
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }


    private fun getTabview(position: Int): View {

        fun getView(string:String,color: ColorStateList?):View{
            val view : View = LayoutInflater.from(context).inflate(R.layout.custom_tab_view,null)
            val text = view.title

            text.setText(string)
            text.setTextColor(color)
            return view
        }

        return when (position) {
            ATTACK_INDEX -> getView(getString(R.string.attack),ContextCompat.getColorStateList(requireContext(),R.color.attackred_option))
            SPEED_INDEX -> getView(getString(R.string.speed),ContextCompat.getColorStateList(requireContext(),R.color.speedblue_option))
            BALANCE_INDEX -> getView(getString(R.string.balance),ContextCompat.getColorStateList(requireContext(),R.color.balancepurple_option))
            DEFENSE_INDEX -> getView(getString(R.string.defense),ContextCompat.getColorStateList(requireContext(),R.color.defensegreen_option))
            SUPPORT_INDEX -> getView(getString(R.string.support),ContextCompat.getColorStateList(requireContext(),R.color.supportorange_option))
            else -> LayoutInflater.from(context).inflate(R.layout.custom_tab_view,null)
        }
    }

}