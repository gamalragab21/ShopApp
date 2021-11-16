package com.developers.shopapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.developers.shopapp.R
import com.developers.shopapp.entities.Splash
import kotlinx.android.synthetic.main.item_splas_screen_view_pager.view.*


class AdapterViewPagerSplash(var list:List<Splash>): RecyclerView.Adapter<AdapterViewPagerSplash.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         var image=itemView.image
         var title=itemView.title
         var descripition=itemView.descripition
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
  return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_splas_screen_view_pager,parent,false))
    }

    override fun getItemCount(): Int {
     return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.setImageResource(list[position].image)
        holder.title.text=list[position].title
        holder.descripition.text=list[position].descripition
    }
}