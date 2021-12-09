package com.developers.shopapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.developers.shopapp.databinding.ItemFavRestaurantBinding
import javax.inject.Inject
import com.developers.shopapp.entities.Restaurant
import com.developers.shopapp.utils.Utils.calculationByDistance
import com.google.android.gms.maps.model.LatLng


class FavRestaurantAdapter @Inject constructor(
    private val glide: RequestManager,
    private val context: Context,
) : RecyclerView.Adapter<FavRestaurantAdapter.FoodViewHolder>() {




    //
    var restaurants: List<Restaurant>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    var laLng: LatLng? =null

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

    inner class FoodViewHolder(val itemBinding:  ItemFavRestaurantBinding) : RecyclerView.ViewHolder(itemBinding.root) {


        fun bindData(item: Restaurant) {
            glide.load(item.imageRestaurant).into(itemBinding.itemFavImage)
            itemBinding.itemFavType.text=item.restaurantType
            itemBinding.itemFavName.text=item.restaurantName
            itemBinding.itemFavRating.text="${item.rateCount}"
            itemBinding.itemFavNumberRating.text="(${item.rateRestaurant?.size})"

            itemBinding.itemFavDistance.text="${calculationByDistance(laLng!!,LatLng(item.latitude,item.latitude))} M "

            setupActions(item)
        }

       private fun setupActions(item: Restaurant){
            itemBinding.root.setOnClickListener {
                onItemClickListener?.let { click->
                    click(item)
                }
            }


        }



    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val itemBinding = ItemFavRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(itemBinding)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {

        val restaurant = restaurants[position]


        holder.apply {
            bindData(restaurant)
        }


    }


    override fun getItemCount(): Int = restaurants.size

    private var onItemClickListener: ((Restaurant) -> Unit)? = null

    fun setOnItemClickListener(listener: (Restaurant) -> Unit) {
        onItemClickListener = listener
    }




}