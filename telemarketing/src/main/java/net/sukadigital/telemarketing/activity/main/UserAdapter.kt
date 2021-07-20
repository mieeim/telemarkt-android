package net.sukadigital.telemarketing.activity.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.google.android.material.card.MaterialCardView
import net.sukadigital.telemarketing.R
import net.sukadigital.telemarketing.model.UserItem
import kotlinx.android.synthetic.main.item_user_card.view.*
import kotlin.random.Random

class UserAdapter(val list: ArrayList<UserItem>, val listener: UserListListener) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView
        val layout: MaterialCardView
        val image: ImageView

        init {
            name = itemView.itemUserCard_name
            layout = itemView.itemUserCard_layout
            image = itemView.itemUserCard_image
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_user_card, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list.get(position)
        holder.name.text = item.name
        holder.layout.setOnClickListener {
            listener.onClickUser(item)
        }
        var initialList = item.name.split(" ")
        var initial = ""
        for (x in initialList) {
            if(initial.length<2) {
                initial += x.get(0)
            }
        }
        var listColor = arrayOf(
            holder.itemView.context.resources.getColor(R.color.green),
            holder.itemView.context.resources.getColor(R.color.grey),
            holder.itemView.context.resources.getColor(R.color.red),
            holder.itemView.context.resources.getColor(R.color.blue),
            holder.itemView.context.resources.getColor(R.color.cyan),
            holder.itemView.context.resources.getColor(R.color.orange),
            holder.itemView.context.resources.getColor(R.color.black),
            holder.itemView.context.resources.getColor(R.color.lightGrey),
            holder.itemView.context.resources.getColor(R.color.teal_200),
            holder.itemView.context.resources.getColor(R.color.teal_700),
            holder.itemView.context.resources.getColor(R.color.purple_200),
            holder.itemView.context.resources.getColor(R.color.green),
            holder.itemView.context.resources.getColor(R.color.grey),
            holder.itemView.context.resources.getColor(R.color.red),
            holder.itemView.context.resources.getColor(R.color.blue),
            holder.itemView.context.resources.getColor(R.color.cyan),
            holder.itemView.context.resources.getColor(R.color.orange),
            holder.itemView.context.resources.getColor(R.color.black),
            holder.itemView.context.resources.getColor(R.color.lightGrey),
            holder.itemView.context.resources.getColor(R.color.teal_200),
            holder.itemView.context.resources.getColor(R.color.teal_700),
            holder.itemView.context.resources.getColor(R.color.purple_200),
            holder.itemView.context.resources.getColor(R.color.green),
            holder.itemView.context.resources.getColor(R.color.grey),
            holder.itemView.context.resources.getColor(R.color.red),
            holder.itemView.context.resources.getColor(R.color.blue),
            holder.itemView.context.resources.getColor(R.color.cyan),
            holder.itemView.context.resources.getColor(R.color.orange),
            holder.itemView.context.resources.getColor(R.color.black),
            holder.itemView.context.resources.getColor(R.color.lightGrey),
            holder.itemView.context.resources.getColor(R.color.teal_200),
            holder.itemView.context.resources.getColor(R.color.teal_700),
            holder.itemView.context.resources.getColor(R.color.purple_200),
            holder.itemView.context.resources.getColor(R.color.green),
            holder.itemView.context.resources.getColor(R.color.grey),
            holder.itemView.context.resources.getColor(R.color.red),
            holder.itemView.context.resources.getColor(R.color.blue),
            holder.itemView.context.resources.getColor(R.color.cyan),
            holder.itemView.context.resources.getColor(R.color.orange),
            holder.itemView.context.resources.getColor(R.color.black),
            holder.itemView.context.resources.getColor(R.color.lightGrey),
            holder.itemView.context.resources.getColor(R.color.teal_200),
            holder.itemView.context.resources.getColor(R.color.teal_700),
            holder.itemView.context.resources.getColor(R.color.purple_200)
        )
        var drawable = TextDrawable.builder().buildRound(initial,listColor.get(Random.nextInt(listColor.size-1)))
        holder.image.setImageDrawable(drawable)

    }

    override fun getItemCount(): Int {
        return list.size
    }

}
