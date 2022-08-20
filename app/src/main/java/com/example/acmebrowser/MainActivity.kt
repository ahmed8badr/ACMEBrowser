package com.example.acmebrowser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.acmebrowser.MainActivity.Companion.myPager
import com.example.acmebrowser.adapters.TabAdapter
import com.example.acmebrowser.databinding.ActivityMainBinding
import com.example.acmebrowser.databinding.BookmarkWindowBinding
import com.example.acmebrowser.databinding.TabsWindowBinding
import com.example.acmebrowser.dataclass.Bookmark
import com.example.acmebrowser.dataclass.Tab
import com.example.acmebrowser.fragments.BrowseFragment
import com.example.acmebrowser.fragments.HomeFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    private var frag: BrowseFragment? = null
    lateinit var binding: ActivityMainBinding

    companion object {
        var tabsList: ArrayList<Tab> = ArrayList()
        lateinit var myPager: ViewPager2
        lateinit var tabsBtn: MaterialTextView
        var bookmarkList: ArrayList<Bookmark> = ArrayList()
        var bookmarkIndex: Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        getBookmarks()

        tabsList.add(Tab("Home", HomeFragment()))

        binding.myPager.isUserInputEnabled = false
        binding.myPager.adapter = TabsAdapter(supportFragmentManager, lifecycle)
        binding.myPager.isUserInputEnabled = false
        myPager = binding.myPager
        tabsBtn = binding.tabsBtn

        initializeView()

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBackPressed() {
        try {
            frag = tabsList[binding.myPager.currentItem].fragment as BrowseFragment
        }catch (e:Exception){}

        when {
            frag?.binding?.webView?.canGoBack() == true -> frag!!.binding.webView.goBack()
            binding.myPager.currentItem != 0 ->{
                binding.logoIcon.setImageResource(R.drawable.ic_baseline_link_24)
                tabsList.removeAt(binding.myPager.currentItem)
                binding.myPager.adapter?.notifyDataSetChanged()
                binding.myPager.currentItem = tabsList.size - 1
            }
            else -> super.onBackPressed()
        }
    }
    private fun onForwardPressed(){
        var frag:BrowseFragment? = null
        try {
            frag = tabsList[binding.myPager.currentItem].fragment as BrowseFragment
        }catch (e:Exception){}
        frag?.apply {
            if(binding.webView.canGoForward())
                binding.webView.goForward()
        }
    }

    private inner class TabsAdapter(fa:FragmentManager,  lc: Lifecycle) : FragmentStateAdapter(fa,lc){
        override fun getItemCount(): Int = tabsList.size
        override fun createFragment(position: Int): Fragment = tabsList[position].fragment
    }

    private fun initializeView() {

        binding.tabsBtn.setOnClickListener{
            val viewTabs = layoutInflater.inflate(R.layout.tabs_window, binding.root,false)
            val bindingTabs = TabsWindowBinding.bind(viewTabs)
            val dialogTabs = MaterialAlertDialogBuilder(this, R.style.roundCornerDialog).setView(viewTabs)
                .setTitle("Select Tab")
                .setPositiveButton("new tab"){self,_->
                    changeTab("Home", HomeFragment())
                    self.dismiss()}
                .create()

            bindingTabs.tabsRV.setHasFixedSize(true)
            bindingTabs.tabsRV.layoutManager = LinearLayoutManager(this)
            bindingTabs.tabsRV.adapter = TabAdapter(this,dialogTabs)

            dialogTabs.show()
            val pBtn = dialogTabs.getButton(AlertDialog.BUTTON_POSITIVE)
            pBtn.isAllCaps = false
            pBtn.setTextColor(Color.BLACK)
            pBtn.setCompoundDrawablesWithIntrinsicBounds(
                ResourcesCompat.getDrawable(resources,R.drawable.ic_baseline_add_24,theme)
                ,null,null,null)
        }
        binding.clearBtn.setOnClickListener{
            binding.inputUrl.setText("")
        }

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.forwardBtn.setOnClickListener{
            onForwardPressed()
        }
        binding.bookmarkBtn.setOnClickListener {
            frag = tabsList[binding.myPager.currentItem].fragment as BrowseFragment
            frag?.let {
                bookmarkIndex = isBookmarked(it.binding.webView.url!!)
                if ( bookmarkIndex == -1){
                val viewBM = layoutInflater.inflate(R.layout.bookmark_window, binding.root, false)
                val bindingBM = BookmarkWindowBinding.bind(viewBM)
                val dialogBM = MaterialAlertDialogBuilder(this)
                    .setTitle("Add Bookmark")
                    .setMessage("Url:${it.binding.webView.url}")
                    .setPositiveButton("add") { self, _ ->
                        try {
                            val array = ByteArrayOutputStream()
                            it.logoIcon?.compress(Bitmap.CompressFormat.PNG, 100, array)
                            bookmarkList.add(
                                Bookmark(name = bindingBM.bookmarkTitle.text.toString(), url = it.binding.webView.url!!, array.toByteArray()))
                        }catch (e:Exception){
                            bookmarkList.add(
                                Bookmark(name = bindingBM.bookmarkTitle.text.toString(), url = it.binding.webView.url!!))
                        }
                        binding.bookmarkBtn.setImageResource(R.drawable.ic_baseline_bookmark_24)
                        self.dismiss() }
                    .setNegativeButton("cancel") { self, _ -> self.dismiss() }
                    .setView(viewBM).create()

                dialogBM.show()
                    bindingBM.bookmarkTitle.setText(it.binding.webView.title)
            }else{
                val dialogBM = MaterialAlertDialogBuilder(this)
                    .setTitle("Remove Bookmark")
                    .setMessage("Url:${it.binding.webView.url}")
                    .setPositiveButton("remove") { self, _ ->
                        bookmarkList.removeAt(bookmarkIndex)
                        binding.bookmarkBtn.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
                        self.dismiss() }
                    .setNegativeButton("cancel") { self, _ -> self.dismiss() }
                    .create()
                    dialogBM.show()
                }
            }
        }
    }

    fun isBookmarked(url: String): Int{
        bookmarkList.forEachIndexed { index, bookmark ->
            if(bookmark.url == url) return index
        }
        return -1
    }
    fun setBookmarks(){
        val editor = getSharedPreferences("BOOKMARKS", MODE_PRIVATE).edit()
        val data = GsonBuilder().create().toJson(bookmarkList)
        editor.putString("bookmarkList", data)
        editor.apply()
    }
    private fun getBookmarks(){
        bookmarkList = ArrayList()
        val editor = getSharedPreferences("BOOKMARKS", MODE_PRIVATE)
        val data = editor.getString("bookmarkList", null)
        if (data != null){
            val list: ArrayList<Bookmark> = GsonBuilder().create().fromJson(data, object: TypeToken<ArrayList<Bookmark>>(){}.type)
            bookmarkList.addAll(list)
        }
    }
}

@SuppressLint("NotifyDataSetChanged")
fun changeTab(url: String, fragment: Fragment, isBackground: Boolean = false){
    MainActivity.tabsList.add(Tab(name = url,fragment = fragment))
    myPager.adapter?.notifyDataSetChanged()
    MainActivity.tabsBtn.text = MainActivity.tabsList.size.toString()
    if(!isBackground) myPager.currentItem = MainActivity.tabsList.size - 1
}