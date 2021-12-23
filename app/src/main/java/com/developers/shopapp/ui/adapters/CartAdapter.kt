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
import javax.inject.Inject
import com.developers.shopapp.entities.ProductCart


class CartAdapter @Inject constructor(
    private val glide: RequestManager,
    private val context: Context,
) : RecyclerView.Adapter<CartAdapter.FoodViewHolder>() {


    val finalPrice: Float = 0f

    //
    var carts: List<ProductCart>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    private val diffCallback = object : DiffUtil.ItemCallback<ProductCart>() {
        override fun areContentsTheSame(oldItem: ProductCart, newItem: ProductCart): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        //
        override fun areItemsTheSame(oldItem: ProductCart, newItem: ProductCart): Boolean {
            return oldItem.foodId == newItem.foodId
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)

    inner class FoodViewHolder(val itemBinding: ItemCartBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {


        fun bindData(item: ProductCart) {
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
            }).into(itemBinding.cartImage)
            itemBinding.cartFoodName.text = item.foodName
            itemBinding.cartFoodPrice.text = "${item.coinType}${item.foodPrice}"
            itemBinding.itemCartMount.text = item.foodQuality.toString()


            setupActions(item)
        }

        private fun setupActions(item: ProductCart) {
            itemBinding.root.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(item)
                }
            }

            itemBinding.add.setOnClickListener {
                onCounterClickListener?.let { click ->
                    click(setAddToCart(false, item), item.coinType,adapterPosition)
                }
            }
            itemBinding.minTotCart.setOnClickListener {
                onCounterClickListener?.let { click ->
                    click(setAddToCart(true, item), item.coinType,adapterPosition)
                }
            }

            itemBinding.cartImage.setOnClickListener {
                onImageClickListener?.let {click->
                    click(item)
                }
            }


        }

        private fun setAddToCart(minis: Boolean, item: ProductCart): ProductCart {
            var countCarts = itemBinding.itemCartMount.text.toString().toInt()
            var itemPrice = item.foodPrice

            if (minis) {
                if (countCarts <= 0) {
                    return item
                } else {
                    countCarts -= 1
                }
            } else {
                countCarts += 1
            }

            itemBinding.itemCartMount.text = countCarts.toString()
            item.foodQuality=countCarts
            return item
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val itemBinding =
            ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(itemBinding)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {

        val cart = carts[position]


        holder.apply {
            bindData(cart)
        }


    }


    override fun getItemCount(): Int = carts.size

    private var onItemClickListener: ((ProductCart) -> Unit)? = null

    fun setOnItemClickListener(listener: (ProductCart) -> Unit) {
        onItemClickListener = listener
    }
private var onImageClickListener: ((ProductCart) -> Unit)? = null

    fun setOnImageClickListener(listener: (ProductCart) -> Unit) {
        onImageClickListener = listener
    }

    private var onCounterClickListener: ((ProductCart, String,Int) -> Unit)? = null

    fun setOnCounterClickListener(listener: (ProductCart, String,Int) -> Unit) {
        onCounterClickListener = listener
    }


}