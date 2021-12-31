package com.developers.shopapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.developers.shopapp.R
import javax.inject.Inject
import com.developers.shopapp.databinding.ItemFoodLayoutBinding
import com.developers.shopapp.entities.Product
import com.developers.shopapp.entities.Restaurant
import com.developers.shopapp.utils.Constants.TAG


class ProductAdapter @Inject constructor(
    private val glide: RequestManager,
    private val context: Context,
) : RecyclerView.Adapter<ProductAdapter.FoodViewHolder>() {





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
            return oldItem.productId == newItem.productId
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)

    inner class FoodViewHolder(val itemBinding:  ItemFoodLayoutBinding) : RecyclerView.ViewHolder(itemBinding.root) {


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
            }).into(itemBinding.itemFoodImageview)
            itemBinding.itemProductDesc.text=item.productDescription
            itemBinding.itemProductName.text=item.productName
            itemBinding.itemProductPrice.text="${item.coinType}${item.productPrice}"

            if (item.inFav==true){
                itemBinding.itemProductSave.setImageResource(R.drawable.saved)
            }else{
                itemBinding.itemProductSave.setImageResource(R.drawable.not_save)
            }

            setupActions(item)
        }

       private fun setupActions(item: Product){
            itemBinding.root.setOnClickListener {
                onItemClickListener?.let { click->
                    click(item)
                }
            }

           itemBinding.itemProductSave.setOnClickListener {
               onSavedClickListener?.let {click->
                   click(item,itemBinding.itemProductSave,adapterPosition)
               }
           }
        }



    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val itemBinding = ItemFoodLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    private var onSavedClickListener: ((Product,ImageView,Int) -> Unit)? = null

    fun setOnSavedClickListener(listener: (Product,ImageView,Int) -> Unit) {
        onSavedClickListener = listener
    }
    private var onContactClickListener: ((Restaurant) -> Unit)? = null

    fun setOnContactClickListener(listener: (Restaurant) -> Unit) {
        onContactClickListener = listener
    }
    fun clearItemAndIfLast(restaurant: Product,position:Int):Boolean{
        val productsList=products.toMutableList()
        productsList.remove(restaurant)
        notifyItemRemoved(position)
        products=productsList.toList()

       return productsList.isEmpty()
//        notifyItemChanged(position)
//        notifyDataSetChanged()
    }

}