package com.unitewikiapp.unitewiki.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.unitewikiapp.unitewiki.views.rankfragments.*


const val ATTACK_INDEX = 0
const val SPEED_INDEX = 1
const val BALANCE_INDEX = 2
const val DEFENSE_INDEX = 3
const val SUPPORT_INDEX = 4

class ViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        ATTACK_INDEX to { AttackPokemonFragment() },
        SPEED_INDEX to { SpeedPokemonFragment() },
        BALANCE_INDEX to { BalancePokemonFragment() },
        DEFENSE_INDEX to { DefensePokemonFragment() },
        SUPPORT_INDEX to { SupportPokemonFragment() }
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
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}