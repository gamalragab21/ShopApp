package com.developers.shopapp.ui.fragments.myOrders

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.developers.shopapp.databinding.FragmentMyOrdersBinding
import com.developers.shopapp.ui.adapters.ViewPagerFragmentOrderAdapter
import com.developers.shopapp.ui.adapters.ViewPagerFragmentTabsAdapter
import com.developers.shopapp.ui.viewmodels.OrdersViewModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@AndroidEntryPoint
class MyOrdersFragment : Fragment() {
    private var _binding: FragmentMyOrdersBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy {
        findNavController()
    }
    private val ordersViewModels: OrdersViewModel by viewModels()


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setTabs()

    }

    private fun setTabs() {

        binding.tabs.addTab(binding.tabs.newTab().setText("Pr-Order"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Oncoming"))
        binding.tabs.addTab(binding.tabs.newTab().setText("History"))


        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager2.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
        setupViewPager(binding.viewPager2)


    }

    private fun setupViewPager(viewPager2: ViewPager2) {

        val adapter = ViewPagerFragmentOrderAdapter(childFragmentManager, lifecycle)
        viewPager2.adapter = adapter

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                binding.tabs.selectTab(binding.tabs.getTabAt(position))
            }

            override fun onPageScrollStateChanged(state: Int) {
                /*empty*/
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyOrdersBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}