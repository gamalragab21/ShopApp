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
import com.developers.shopapp.databinding.ItemCartBinding
import com.developers.shopapp.databinding.ItemOrderLayoutBinding
import com.developers.shopapp.entities.Order
import javax.inject.Inject
import com.developers.shopapp.entities.ProductCart


class OrderAdapter @Inject constructor(
    private val glide: RequestManager,
    private val context: Context,
) : RecyclerView.Adapter<OrderAdapter.FoodViewHolder>() {



    //
    var orders: List<Order>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    private val diffCallback = object : DiffUtil.ItemCallback<Order>() {
        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        //
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.foodId == newItem.foodId
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)

    inner class FoodViewHolder(val itemBinding: ItemOrderLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {


        fun bindData(item: Order) {
            glide.load(item.foodImage).listener(object : RequestListener<Drawable?> {
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
            }).into(itemBinding.itemImageOrder)
            itemBinding.foodOrderName.text = item.productName
            itemBinding.orderFoodPrice.text = "${item.coinType}${(item.productPrice-item.productDistCount)}"


            setupActions(item)
        }

        private fun setupActions(item: Order) {
            itemBinding.root.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(item)
                }
            }



            itemBinding.itemImageOrder.setOnClickListener {
                onImageClickListener?.let {click->
                    click(item)
                }
            }


        }



    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val itemBinding =
            ItemOrderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(itemBinding)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {

        val order = orders[position]


        holder.apply {
            bindData(order)
        }


    }


    override fun getItemCount(): Int = orders.size

    private var onItemClickListener: ((Order) -> Unit)? = null

    fun setOnItemClickListener(listener: (Order) -> Unit) {
        onItemClickListener = listener
    }
private var onImageClickListener: ((Order) -> Unit)? = null

    fun setOnImageClickListener(listener: (Order) -> Unit) {
        onImageClickListener = listener
    }



}