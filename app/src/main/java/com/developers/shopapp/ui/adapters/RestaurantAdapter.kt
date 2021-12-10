package com.developers.shopapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import javax.inject.Inject
import com.developers.shopapp.R
import com.developers.shopapp.databinding.ItemRecentLayoutBinding
import com.developers.shopapp.entities.Restaurant
import com.developers.shopapp.utils.Utils.calculationByDistance
import com.developers.shopapp.utils.Utils.getTimeAgo
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.item_recent_layout.view.*
import javax.sql.DataSource


class RestaurantAdapter @Inject constructor(
    private val glide: RequestManager,
    private val context: Context,
) : RecyclerView.Adapter<RestaurantAdapter.SavedViewHolder>() {


//    var countries: List<CovidModelItem> =ArrayList<CovidModelItem>()

    var laLng:LatLng? =null


    //
    var restaurantes: List<Restaurant>
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

    inner class SavedViewHolder(val itemBinding:  ItemRecentLayoutBinding) : RecyclerView.ViewHolder(itemBinding.root) {


        fun bindData(item: Restaurant) {
            glide.load(item.imageRestaurant).listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    itemBinding.progressShimmer.stopShimmer()
                    itemBinding.progressShimmer.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                    dataSource: com.bumptech.glide.load.DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    itemBinding.progressShimmer.stopShimmer()
                    itemBinding.progressShimmer.visibility = View.GONE

                    return false
                }
            }).into(itemBinding.itemRecentImageview)
            itemBinding.itemRecentType.text=item.restaurantType
            itemBinding.itemRecentTime.text=getTimeAgo(item.createAt,context)

            itemBinding.itemRecentRating.text="${item.rateCount}"

            laLng?.let {
                val resultDistanceKM=calculationByDistance(it,LatLng(item.latitude,item.longitude))
                itemBinding.itemRecentDistance.text="$resultDistanceKM KM"

            }
            itemBinding.itemRecentName.text=item.restaurantName
            if (item.inFav != false){
                itemBinding.itemRecentSave.setImageResource(R.drawable.saved)
            }else{
                itemBinding.itemRecentSave.setImageResource(R.drawable.not_save)
            }

            setupActions(item)

        }
        private fun setupActions(restaurant: Restaurant) {
            itemBinding.root.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(restaurant)
                }
            }
            itemBinding.itemRecentSave.setOnClickListener {
                onSavedClickListener?.let { clcik->
                    clcik(restaurant,itemBinding.itemRecentSave,adapterPosition)
                }
            }
            itemBinding.itemRecentContact.setOnClickListener {
                onContactClickListener?.let { clcik->
                    clcik(restaurant)
                }
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedViewHolder {
        val itemBinding = ItemRecentLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SavedViewHolder(itemBinding)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: SavedViewHolder, position: Int) {

        val restaurant = restaurantes[position]


        holder.apply {
            bindData(restaurant)
        }
    }


    override fun getItemCount(): Int = restaurantes.size

    private var onItemClickListener: ((Restaurant) -> Unit)? = null

    fun setOnItemClickListener(listener: (Restaurant) -> Unit) {
        onItemClickListener = listener
    }

    private var onSavedClickListener: ((Restaurant,ImageView,Int) -> Unit)? = null

    fun setOnSavedClickListener(listener: (Restaurant,ImageView,Int) -> Unit) {
        onSavedClickListener = listener
    }
    private var onContactClickListener: ((Restaurant) -> Unit)? = null

    fun setOnContactClickListener(listener: (Restaurant) -> Unit) {
        onContactClickListener = listener
    }
    fun clearItemAndIfLast(restaurant: Restaurant,position:Int):Boolean{
        val restaurantList=restaurantes.toMutableList()
        restaurantList.remove(restaurant)
        notifyItemRemoved(position)
        restaurantes=restaurantList.toList()

       return restaurantList.isEmpty()
//        notifyItemChanged(position)
//        notifyDataSetChanged()
    }

}