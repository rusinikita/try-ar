package com.nikita.tryar.item_info

import android.support.annotation.DrawableRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.nikita.tryar.R
import com.squareup.picasso.Picasso

class PhotoViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    private val image = view.findViewById(R.id.photo_item) as ImageView

    fun bind(@DrawableRes imageId: Int) = with(view) {
        Picasso.with(view.context).load(imageId).into(image)
//        image.setImageResource(imageId)
    }
}

class PhotoAdapter(val items: List<Int>) : RecyclerView.Adapter<PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) = holder.bind(items[position])
}
