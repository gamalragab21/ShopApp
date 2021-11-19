package com.developers.shopapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import javax.inject.Inject
import com.developers.shopapp.R
import com.developers.shopapp.entities.Product
import com.developers.shopapp.entities.ProductData
import kotlinx.android.synthetic.main.item_recent_layout.view.*


class RecentAdapter @Inject constructor(
    private val glide: RequestManager,
    private val context: Context,
) : RecyclerView.Adapter<RecentAdapter.SavedViewHolder>() {


//    var countries: List<CovidModelItem> =ArrayList<CovidModelItem>()

    //
    var producties: List<ProductData>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    private val diffCallback = object : DiffUtil.ItemCallback<ProductData>() {
        override fun areContentsTheSame(oldItem: ProductData, newItem: ProductData): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        //
        override fun areItemsTheSame(oldItem: ProductData, newItem: ProductData): Boolean {
            return oldItem.id == newItem.id
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

        fun bindData(item: ProductData) {
            glide.load(item.image).into(item_recent_imageview)
            item_recent_name.text=item.name
            if (item.inFavorites){
                item_recent_save.setImageResource(R.drawable.saved)
            }else{
                item_recent_save.setImageResource(R.drawable.not_save)
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

        val product = producties[position]


        holder.apply {
            bindData(product)
        }
    }


    override fun getItemCount(): Int = producties.size

    private var onItemClickListener: ((ProductData) -> Unit)? = null

    fun setOnItemClickListener(listener: (ProductData) -> Unit) {
        onItemClickListener = listener
    }


}