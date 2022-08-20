package com.example.acmebrowser.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.acmebrowser.MainActivity
import com.example.acmebrowser.databinding.TabBinding
import com.google.android.material.snackbar.Snackbar

class TabAdapter(private val context: Context, private val dialog: AlertDialog): RecyclerView.Adapter<TabAdapter.MyHolder>() {

    class MyHolder(binding: TabBinding) :RecyclerView.ViewHolder(binding.root) {
        val delTab = binding.delTab
        val name = binding.tabName
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(TabBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = MainActivity.tabsList[position].name
        holder.root.setOnClickListener{
            MainActivity.myPager.currentItem = position
            dialog.dismiss()
        }
        holder.delTab.setOnClickListener{
            if(MainActivity.tabsList.size == 1 || position == MainActivity.myPager.currentItem)
                Snackbar.make(MainActivity.myPager,"Can't Remove current tab",3000).show()
            else{
                MainActivity.tabsList.removeAt(position)
                notifyDataSetChanged()
                MainActivity.myPager.adapter?.notifyItemRemoved(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return MainActivity.tabsList.size
    }
}