package com.imanfz.utility.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Created by Iman Faizal on 09/Jun/2022
 **/

class TabPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    private var fragmentList = arrayListOf<Fragment>()
    private var listTitle = arrayListOf<String>()

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment = fragmentList[position]

    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        listTitle.add(title)
    }

    fun getTabTitle(position: Int) : String = listTitle[position]
}