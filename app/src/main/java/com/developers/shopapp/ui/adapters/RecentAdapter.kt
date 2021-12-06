package com.developers.shopapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import javax.inject.Inject
import com.developers.shopapp.R
import com.developers.shopapp.entities.Restaurant
import com.developers.shopapp.utils.Constants
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.item_recent_layout.view.*


class RecentAdapter @Inject constructor(
    private val glide: RequestManager,
    private val context: Context,
) : RecyclerView.Adapter<RecentAdapter.SavedViewHolder>() {


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

    inner class SavedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item_recent_imageview = itemView.item_recent_imageview
        val item_recent_name = itemView.item_recent_name
        val item_recent_save = itemView.item_recent_save
        val item_recent_type = itemView.item_recent_type
        val item_recent_distance = itemView.item_recent_distance
        val item_recent_rating = itemView.item_recent_rating
        val item_recent_time = itemView.item_recent_time
        val item_recent_contact = itemView.item_recent_contact

        fun bindData(item: Restaurant,position: Int) {
            glide.load(item.imageRestaurant).into(item_recent_imageview)
            item_recent_type.text=item.restaurantType
            item_recent_time.text=Constants.getTimeAgo(item.createAt,context)

            item_recent_rating.text="${item.rateCount}"

            laLng?.let {
                val resultDistanceKM=Constants.CalculationByDistance(it,LatLng(item.latitude,item.longitude))
                item_recent_distance.text="$resultDistanceKM KM"

            }
            item_recent_name.text=item.restaurantName
            if (item.inFav != false){
                item_recent_save.setImageResource(R.drawable.saved)
            }else{
                item_recent_save.setImageResource(R.drawable.not_save)
            }

            item_recent_save.setOnClickListener {
                onSavedClickListener?.let { clcik->
                    clcik(item,item_recent_save)

                }
            }

        }




    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedViewHolder {
        return SavedViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_recent_layout,
                parent,
                false
            )
        )
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: SavedViewHolder, position: Int) {

        val product = restaurantes[position]


        holder.apply {
            bindData(product,position)
        }

    }


    override fun getItemCount(): Int = restaurantes.size

    private var onItemClickListener: ((Restaurant) -> Unit)? = null

    fun setOnItemClickListener(listener: (Restaurant) -> Unit) {
        onItemClickListener = listener
    }

    private var onSavedClickListener: ((Restaurant,ImageView) -> Unit)? = null

    fun setOnSavedClickListener(listener: (Restaurant,ImageView) -> Unit) {
        onSavedClickListener = listener
    }

}