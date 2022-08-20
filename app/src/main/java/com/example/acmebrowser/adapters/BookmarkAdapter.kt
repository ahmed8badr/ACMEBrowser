package com.example.acmebrowser.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.example.acmebrowser.MainActivity
import com.example.acmebrowser.changeTab
import com.example.acmebrowser.databinding.BookmarkViewBinding
import com.example.acmebrowser.fragments.BrowseFragment

class BookmarkAdapter(private val context: Context):
    RecyclerView.Adapter<BookmarkAdapter.MyHolder>() {
    class MyHolder(binding: BookmarkViewBinding): RecyclerView.ViewHolder(binding.root) {
        val icon = binding.bookmarkIcon
        val name = binding.bookmarkName
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(binding = BookmarkViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        try {
            val icon = BitmapFactory.decodeByteArray(MainActivity.bookmarkList[position].image,0,
                MainActivity.bookmarkList[position].image!!.size)
            holder.icon.background = icon.toDrawable(context.resources)
        }catch (e: Exception){
            holder.icon.text = MainActivity.bookmarkList[position].name[0].toString()
        }

        holder.name.text = MainActivity.bookmarkList[position].name
        holder.root.setOnClickListener{
            context as MainActivity
            changeTab(MainActivity.bookmarkList[position].name,BrowseFragment(urlNew = MainActivity.bookmarkList[position].url))
        }
    }

    override fun getItemCount(): Int {
        return MainActivity.bookmarkList.size
    }
}