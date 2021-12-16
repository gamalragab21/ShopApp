package com.developers.shopapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
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
import com.developers.shopapp.R
import javax.inject.Inject
import com.developers.shopapp.databinding.ItemReviewLayoutBinding
import com.developers.shopapp.entities.RateProduct
import com.developers.shopapp.utils.Utils.getTimeAgo
import kotlinx.coroutines.DelicateCoroutinesApi


class ReviewAdapter @Inject constructor(
    private val glide: RequestManager,
    private val context: Context
) : RecyclerView.Adapter<ReviewAdapter.FoodViewHolder>() {




    //
    var rateProducts: List<RateProduct>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    private val diffCallback = object : DiffUtil.ItemCallback<RateProduct>() {
        override fun areContentsTheSame(oldItem: RateProduct, newItem: RateProduct): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        //
        override fun areItemsTheSame(oldItem: RateProduct, newItem: RateProduct): Boolean {
            return oldItem.productId == newItem.productId
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)

    inner class FoodViewHolder(val itemBinding:  ItemReviewLayoutBinding) : RecyclerView.ViewHolder(itemBinding.root) {


        fun bindData(item: RateProduct) {
            glide.load(item.user!!.image).listener(object : RequestListener<Drawable?> {
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
            }).into(itemBinding.imageUser)

            itemBinding.textDescripition.text=item.messageRate
            itemBinding.createAt.text=getTimeAgo(item.countRate.toLong(),context)
            itemBinding.ratingCount.rating=item.countRate.toFloat()
            itemBinding.userName.text=item.user!!.username

            itemBinding.textReadMore.setOnClickListener {
                if ( itemBinding.textReadMore.text.toString() == context.getString(R.string.read_more)) {
                    itemBinding.textDescripition.maxLines = Int.MAX_VALUE
                    itemBinding.textDescripition.ellipsize = null
                    itemBinding.textReadMore.text = context.getString(R.string.read_less)
                } else {
                    itemBinding.textDescripition.maxLines = 4
                    itemBinding.textDescripition.ellipsize = TextUtils.TruncateAt.END
                    itemBinding.textReadMore.text = context.getString(R.string.read_more)
                }
            }
        }





    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val itemBinding = ItemReviewLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(itemBinding)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {

        val rateProduct = rateProducts[position]


        holder.apply {
            bindData(rateProduct)
        }


    }


    override fun getItemCount(): Int = rateProducts.size



}