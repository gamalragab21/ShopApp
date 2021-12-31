package com.developers.shopapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.developers.shopapp.R
import com.developers.shopapp.databinding.ItemCartBinding
import com.developers.shopapp.databinding.ItemOrderLayoutBinding
import com.developers.shopapp.entities.Order
import javax.inject.Inject
import com.developers.shopapp.entities.ProductCart
import com.developers.shopapp.entities.Restaurant
import com.developers.shopapp.utils.Utils.getDate
import com.developers.shopapp.utils.Utils.getTimeAgo
import java.text.SimpleDateFormat
import java.util.*


class OrderAdapter @Inject constructor(
    private val glide: RequestManager,
    private val context: Context,
) : RecyclerView.Adapter<OrderAdapter.FoodViewHolder>() {



    //
    var showReorder: Int=-1

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
            when(showReorder){
                -1 ->{
//                   itemBinding.reorderLn.background=ContextCompat.getDrawable(context,R.drawable.delete_cart_layout)
//                    itemBinding.tvReorder.text="Delete"
//                    itemBinding.imageReorder.setImageResource(R.drawable.close)
                }

                0->{


                    itemBinding.reorderLn.background=ContextCompat.getDrawable(context,R.drawable.delete_cart_layout)
                    itemBinding.tvReorder.text="Delete"
                    itemBinding.imageReorder.setImageResource(R.drawable.close)
//                    itemBinding.reorderLn.background=ContextCompat.getDrawable(context,R.drawable.add_to_cart_layout2)
//                    itemBinding.imageReorder.setImageResource(R.drawable.close)
                }

                1->{
                    itemBinding.tvReorder.text="Tracing"
                    itemBinding.imageReorder.setImageResource(R.drawable.tracking)
                }
            }
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
            itemBinding.itemDateTime.text=getTimeAgo(item.createAt,context)

            setupActions(item)

            itemBinding.headerDate.text= getDate(context,item.createAt)
        }

        private fun setupActions(item: Order) {
            itemBinding.root.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(item)
                }
            }



            itemBinding.reorderLn.setOnClickListener {
                onReOrderClickListener?.let {click->
                    click(item,adapterPosition)
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
private var onReOrderClickListener: ((Order,Int) -> Unit)? = null

    fun setOnReOrderClickListener(listener: (Order,Int) -> Unit) {
        onReOrderClickListener = listener
    }

    fun clearItemAndIfLast(order: Order, position:Int):Boolean{
        val ordersList=orders.toMutableList()
        ordersList.remove(order)
        notifyItemRemoved(position)
        orders=ordersList.toList()

        return ordersList.isEmpty()
//        notifyItemChanged(position)
//        notifyDataSetChanged()
    }

}