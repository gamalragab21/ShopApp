package com.developers.shopapp.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.developers.shopapp.ui.fragments.myOrders.HistoryOrdersFragment
import com.developers.shopapp.ui.fragments.myOrders.OnComingOrderFragment
import com.developers.shopapp.ui.fragments.myOrders.PreOrdersFragment
import com.developers.shopapp.ui.fragments.tabs.*
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class ViewPagerFragmentOrderAdapter(
     fragmentManger: FragmentManager,
     lifecycle: Lifecycle,
) :
    FragmentStateAdapter(fragmentManger, lifecycle) {


    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> {
               PreOrdersFragment()
            }
            1 -> {
                OnComingOrderFragment()
            }
            2 -> {
                HistoryOrdersFragment()
            }
            else -> {
                PreOrdersFragment()
            }
        }
    }


}