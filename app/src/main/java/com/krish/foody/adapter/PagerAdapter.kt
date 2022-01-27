package com.krish.foody.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(
    private val resultBundle: Bundle,
    private val fragments: ArrayList<Fragment>,
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        fragments[position].arguments = resultBundle
        return fragments[position]
    }
}