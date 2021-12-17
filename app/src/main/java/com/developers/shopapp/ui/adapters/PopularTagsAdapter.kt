package com.developers.shopapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.developers.shopapp.R
import com.developers.shopapp.databinding.FragmentRestaurantSearchBinding
import com.developers.shopapp.databinding.ItemPopularTagsBinding
import javax.inject.Inject
import com.developers.shopapp.databinding.ItemReviewLayoutBinding
import com.developers.shopapp.databinding.ItemSearchRecentBinding
import com.developers.shopapp.entities.Product
import com.developers.shopapp.entities.RateProduct
import com.developers.shopapp.entities.Restaurant
import com.developers.shopapp.entities.SearchHistory
import com.developers.shopapp.utils.Constants.TAG
import com.developers.shopapp.utils.Utils.getTimeAgo
import kotlinx.coroutines.DelicateCoroutinesApi


class PopularTagsAdapter @Inject constructor(
    private val glide: RequestManager,
    private val context: Context
) : RecyclerView.Adapter<PopularTagsAdapter.FoodViewHolder>() {


     var viewAll:Boolean=true

    //
    var popularTags: List<Restaurant>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    private val diffCallback = object : DiffUtil.ItemCallback<Restaurant>() {
        override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        //
        override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem.restaurantId == newItem.restaurantId
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)

    inner class FoodViewHolder(val binding: ItemPopularTagsBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bindData(item: Restaurant) {

            binding.tags.text = item.restaurantName

            setupActions(item)
        }

        private fun setupActions(item: Restaurant) {

            binding.root.setOnClickListener {
                onItemClickListener?.let {click->
                    click(item)
                }
            }

        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding =
            ItemPopularTagsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {

        val tag = popularTags[position]


        holder.apply {
            bindData(tag)
        }


    }


    override fun getItemCount(): Int  {
        return if (viewAll){
            if (popularTags.size<=3){
                popularTags.size
            }else{
                3
            }
        }else{
            popularTags.size

        }
    }


        private var onItemClickListener: ((Restaurant) -> Unit)? = null

        fun setOnItemClickListener(listener: (Restaurant) -> Unit) {
            onItemClickListener = listener
        }





}