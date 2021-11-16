package com.developers.shopapp.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.developers.shopapp.R
import com.developers.shopapp.databinding.FragmentSplashBinding
import com.developers.shopapp.entities.Splash
import com.developers.shopapp.ui.adapters.AdapterViewPagerSplash
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {


    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val listSplash by lazy {
        ArrayList<Splash>()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupBoundsImage()

        initViewPagerList()


        // actions
        binding.next.setOnClickListener {
            binding.viewPager2.setCurrentItem(binding.viewPager2.currentItem + 1, true);

        }

        binding.skip.setOnClickListener {
            navigateToHomeFragment(savedInstanceState)
        }

        binding.started.setOnClickListener {
            navigateToHomeFragment(savedInstanceState)
        }

    }

    private fun setupBoundsImage() {
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        //set animation for logo
        lifecycleScope.launch {


            val bounce: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce);
            binding.logoBounds.animation = bounce;

//            Handler().postDelayed(Runnable { //show buttons
//                binding.content.visibility = View.VISIBLE
//                binding.logoBounds.visibility = View.GONE
//            }, 4000)
            delay(4000)
            binding.content.visibility = View.VISIBLE
            binding.logoBounds.visibility = View.GONE

        }
    }

    private fun navigateToHomeFragment(savedInstanceState: Bundle?) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.splashFragment, true)
            .build()
        findNavController().navigate(
            R.id.action_splashFragment_to_homeFragment,
            savedInstanceState,
            navOptions
        )
    }

    private fun initViewPagerList() {
        listSplash.add(
            Splash(
                R.drawable.img1,
                "Fresh Food",
                "As experts in the food retail business, we saw a gap in the market for an upscale gourmet supermarket, which sources premium ingredients and products from all of the over the world, and also has gourmet eateries on its site, along came the idea of Fresh Food Market ‘a gourmet retailer."
            )
        )


        listSplash.add(
            Splash(
                R.drawable.delevery,
                "Faster Delivery",
                "We make food ordering fast,simple and free-no matter if you" +
                        " order online or crash. "
            )
        )
        listSplash.add(
                Splash(
                    R.drawable.img3,
                    "Easy Payment",
                    "Now use your Easypaisa app to order and pay for your food from the comfort of your home."
                )
                )
        binding.viewPager2.adapter = AdapterViewPagerSplash(listSplash)

        initIndicators()
        subscribeToViewPager()
    }

    private fun subscribeToViewPager() {
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                binding.indictor.onPageScrolled(position, positionOffset, positionOffsetPixels)

            }

            override fun onPageSelected(position: Int) {
                binding.indictor.onPageSelected(position)
                if (position == 2) {
                    binding.started.visibility = View.VISIBLE
                    binding.skip.visibility = View.GONE
                    binding.next.visibility = View.GONE
                } else {
                    binding.started.visibility = View.INVISIBLE
                    binding.skip.visibility = View.VISIBLE
                    binding.next.visibility = View.VISIBLE
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                /*empty*/
            }
        })
    }

    private fun initIndicators() {
        binding.indictor.apply {
            setSliderColor(
                R.color.colorPrimaryLight,
                R.color.colorPrimaryDark
            )
            setSliderWidth(40F)
            setSliderHeight(10f)
            setCheckedColor(R.color.colorPrimaryDark)
            setNormalColor(R.color.colorPrimaryLight)
            setSlideMode(IndicatorSlideMode.SMOOTH)
            setIndicatorStyle(IndicatorStyle.ROUND_RECT)
            setPageSize(binding.viewPager2.adapter!!.itemCount)
            notifyDataChanged()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}