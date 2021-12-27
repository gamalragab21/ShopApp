package com.developers.shopapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.developers.shopapp.databinding.ItemPopularFoodBinding
import com.developers.shopapp.entities.Product
import javax.inject.Inject


class PopularFoodAdapter @Inject constructor(
    private val glide: RequestManager,
    private val context: Context,
) : RecyclerView.Adapter<PopularFoodAdapter.FoodViewHolder>() {




    //
    var products: List<Product>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        //
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.restaurantId == newItem.restaurantId
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)

    inner class FoodViewHolder(val itemBinding:  ItemPopularFoodBinding) : RecyclerView.ViewHolder(itemBinding.root) {


        fun bindData(item: Product) {
            glide.load(item.images[0].imageProduct).listener(object : RequestListener<Drawable?> {
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
            }).into(itemBinding.itemPopularImage)
            itemBinding.itemPopularName.text=item.productName
            itemBinding.priceItem.text="${item.coinType}${item.productPrice}"
            itemBinding.ratingCount.rating=item.rateCount!!.toFloat()

            setupActions(item)
        }

       private fun setupActions(item: Product){
            itemBinding.root.setOnClickListener {
                onItemClickListener?.let { click->
                    click(item)
                }
            }

           itemBinding.addToCart.setOnClickListener {
               onItemAddCartClickListener?.let { click->
                   click(item)
               }
           }

        }



    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val itemBinding = ItemPopularFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(itemBinding)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {

        val product = products[position]


        holder.apply {
            bindData(product)
        }


    }


    override fun getItemCount(): Int = products.size

    private var onItemClickListener: ((Product) -> Unit)? = null

    fun setOnItemClickListener(listener: (Product) -> Unit) {
        onItemClickListener = listener
    }

    private var onItemAddCartClickListener: ((Product) -> Unit)? = null

    fun setOnItemAddCartClickListener(listener: (Product) -> Unit) {
        onItemAddCartClickListener = listener
    }


}