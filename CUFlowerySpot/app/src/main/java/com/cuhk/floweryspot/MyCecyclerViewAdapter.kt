package com.cuhk.floweryspot


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView


class MyRecyclerViewAdapter internal constructor(context: Context, private val drawableIds: Array<Int>) : RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {

    private val inflater: LayoutInflater
    private var itemClickListener: ItemClickListener? = null
    private var itemCreateContextMenuListener: ItemCreateContextMenuListener? = null

    init {
        this.inflater = LayoutInflater.from(context)
    }

    // inflates the cell layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.grid_item, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each cell
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setBackgroundResource(drawableIds[position])
    }

    // total number of cells
    override fun getItemCount(): Int {
        return drawableIds.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnCreateContextMenuListener {

        internal var imageView: ImageView = itemView.findViewById(R.id.grid_item_image)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnCreateContextMenuListener(this)
        }

        override fun onClick(view: View) {
            if (itemClickListener != null) itemClickListener!!.onItemClick(view, adapterPosition)
        }

        override fun onCreateContextMenu(menu: ContextMenu?, view: View, menuInfo: ContextMenu.ContextMenuInfo?) {
            if (itemCreateContextMenuListener != null) itemCreateContextMenuListener!!.onItemCreateContextMenu(view, adapterPosition, menu, menuInfo)
        }

    }

    // convenience method for getting data at click position
    internal fun getItem(id: Int): Int {
        return drawableIds[id]
    }

    // allows clicks events to be caught
    internal fun setClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    // allows clicks events to be caught
    internal fun setCreateContextMenuListener(itemCreateContextMenuListener: ItemCreateContextMenuListener) {
        this.itemCreateContextMenuListener = itemCreateContextMenuListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    // parent activity will implement this method to respond to click events
    interface ItemCreateContextMenuListener {
        fun onItemCreateContextMenu(view: View, position: Int, menu: ContextMenu?, menuInfo: ContextMenu.ContextMenuInfo?)
    }
}