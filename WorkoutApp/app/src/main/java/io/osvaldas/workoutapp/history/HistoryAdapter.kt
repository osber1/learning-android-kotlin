package io.osvaldas.workoutapp.history

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.osvaldas.workoutapp.databinding.ItemHistoryRowBinding

class HistoryAdapter(private val items: ArrayList<String>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemHistoryRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val llHistoryItemMain = binding.llHistoryItemMain
        val tvPosition = binding.tvPosition
        val tvItem = binding.tvItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHistoryRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvItem.text = items[position]
        holder.tvPosition.text = (position + 1).toString()

        if (position % 2 == 0) {
            holder.llHistoryItemMain.setBackgroundColor(Color.GRAY)
        } else {
            holder.llHistoryItemMain.setBackgroundColor(Color.WHITE)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}