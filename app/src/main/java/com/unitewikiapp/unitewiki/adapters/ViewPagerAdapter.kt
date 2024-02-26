package com.unitewikiapp.unitewiki.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.unitewikiapp.unitewiki.utils.Constants
import com.unitewikiapp.unitewiki.views.fragments.PokemonRankFragment


const val ATTACK_INDEX = 0
const val SPEED_INDEX = 1
const val BALANCE_INDEX = 2
const val DEFENSE_INDEX = 3
const val SUPPORT_INDEX = 4

class ViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    private val tabFragmentsCreators: Map<Int, String> = mapOf(
        ATTACK_INDEX to Constants.ATTACK_RANKING,
        SPEED_INDEX to Constants.SPEED_RANKING,
        BALANCE_INDEX to Constants.BALANCE_RANKING,
        DEFENSE_INDEX to Constants.DEFENSE_RANKING,
        SUPPORT_INDEX to Constants.SUPPORT_RANKING
    )

    override fun onBindViewHolder(
        holder: FragmentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        val rankingType = tabFragmentsCreators[position]
            ?: throw IndexOutOfBoundsException("Position not valid")
        return PokemonRankFragment.newInstance(rankingType)
    }
}