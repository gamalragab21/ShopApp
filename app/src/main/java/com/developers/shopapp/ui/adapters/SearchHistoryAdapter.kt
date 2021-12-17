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
import javax.inject.Inject
import com.developers.shopapp.databinding.ItemReviewLayoutBinding
import com.developers.shopapp.databinding.ItemSearchRecentBinding
import com.developers.shopapp.entities.Product
import com.developers.shopapp.entities.RateProduct
import com.developers.shopapp.entities.SearchHistory
import com.developers.shopapp.utils.Constants.TAG
import com.developers.shopapp.utils.Utils.getTimeAgo
import kotlinx.coroutines.DelicateCoroutinesApi


class SearchHistoryAdapter @Inject constructor(
    private val glide: RequestManager,
    private val context: Context
) : RecyclerView.Adapter<SearchHistoryAdapter.FoodViewHolder>() {


     var viewMore:Boolean=true

    //
    var searchHistories: List<SearchHistory>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    private val diffCallback = object : DiffUtil.ItemCallback<SearchHistory>() {
        override fun areContentsTheSame(oldItem: SearchHistory, newItem: SearchHistory): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        //
        override fun areItemsTheSame(oldItem: SearchHistory, newItem: SearchHistory): Boolean {
            return oldItem.id == newItem.id
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)

    inner class FoodViewHolder(val binding: ItemSearchRecentBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bindData(item: SearchHistory) {

            binding.searchName.text = item.searchName

            setupActions(item)
        }

        private fun setupActions(item: SearchHistory) {
            binding.root.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(item)
                }
            }

            binding.deleteItem.setOnClickListener {
                onCloseImageClickListener?.let { click ->
                    click(item)
                }
            }
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding =
            ItemSearchRecentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {

        val searchHistory = searchHistories[position]


        holder.apply {
            bindData(searchHistory)
        }


    }


    override fun getItemCount(): Int  {
        return if (viewMore){
            if (searchHistories.size<=3){
                searchHistories.size
            }else{
                3
            }
        }else{
            searchHistories.size

        }
    }


        private var onItemClickListener: ((SearchHistory) -> Unit)? = null

        fun setOnItemClickListener(listener: (SearchHistory) -> Unit) {
            onItemClickListener = listener
        }

        private var onCloseImageClickListener: ((SearchHistory) -> Unit)? = null

        fun setonCloseImageClickListener(listener: (SearchHistory) -> Unit) {
            onCloseImageClickListener = listener
        }




}