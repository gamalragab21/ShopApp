package com.developers.shopapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.developers.shopapp.R
import com.developers.shopapp.databinding.ItemContainerSliderImageBinding
import com.developers.shopapp.databinding.ItemFoodLayoutBinding
import com.developers.shopapp.entities.ProductImage
import java.util.*
import javax.inject.Inject


class ImageSliderAdapter @Inject constructor(
    private val glide : RequestManager,
    private val context : Context,
) : RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder>() {


    var images : List<ProductImage>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private val diffCallback = object : DiffUtil.ItemCallback<ProductImage>() {
        override fun areContentsTheSame(oldItem : ProductImage , newItem : ProductImage) : Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem : ProductImage , newItem : ProductImage) : Boolean {
            return oldItem== newItem
        }
    }
    private val differ = AsyncListDiffer(this , diffCallback)

    inner class ImageSliderViewHolder(val itemBinding : ItemContainerSliderImageBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bindData(image: String) {

            glide.load(image).into(itemBinding.imageSliderContent)


            itemView.setOnClickListener {
                onItemClickListener?.let { click->
                    click(image)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : ImageSliderViewHolder {
        val itemBinding = ItemContainerSliderImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ImageSliderViewHolder(
           itemBinding
        )
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder : ImageSliderViewHolder , position : Int) {
        val image = images[position]
        holder.apply {

            bindData(image.imageProduct)
        }
    }


    override fun getItemCount() : Int = images.size

    private var onItemClickListener : ((String) -> Unit)? = null

    fun setOnItemClickListener(listener : (String) -> Unit) {
        onItemClickListener = listener
    }


}