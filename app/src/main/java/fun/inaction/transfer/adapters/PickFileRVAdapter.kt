package `fun`.inaction.transfer.adapters

import `fun`.inaction.transfer.R
import `fun`.inaction.transfer.bean.PickFileItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PickFileRVAdapter:RecyclerView.Adapter<PickFileRVAdapter.ViewHolder> {

    val itemList:List<PickFileItem>

    constructor(itemList:List<PickFileItem>):super(){
        this.itemList = itemList
    }

    class ViewHolder(val view:View) :RecyclerView.ViewHolder(view){

        val imageView:ImageView
        val textView:TextView

        init {
            imageView = view.findViewById(R.id.imageView)
            textView = view.findViewById(R.id.textView)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pick_file,parent,false)
        val viewHolder = ViewHolder(view)

        viewHolder.view.setOnClickListener {
            val item = itemList[viewHolder.adapterPosition]
            item.onClickListener()
        }

        return viewHolder
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        with(holder){
            imageView.setImageResource(item.imageId)
            textView.text = item.text
        }
    }
}