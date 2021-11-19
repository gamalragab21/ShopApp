package com.developers.shopapp.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.developers.shopapp.ui.fragments.tabs.*

class ViewPagerFragmentAdapter
constructor(fragmentManger: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManger, lifecycle) {


    override fun getItemCount(): Int = 6

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> {
                RecentFragment()
            }
            1 -> {
                FavouriteFragment()
            }
            2 -> {
                RatingFragment()
            }
            3 -> {
                PopularFragment()
            }
            4 -> {
                TrendsFragment()
            }
            5 -> {
                NotificationFragment()
            }
            else -> {
                RecentFragment()
            }
        }
    }


}