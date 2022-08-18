package com.example.acmebrowser

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.acmebrowser.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    companion object{
        var tabsList: ArrayList<Fragment> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        tabsList.add(HomeFragment())

        binding.myPager.isUserInputEnabled = false
        binding.myPager.adapter = TabsAdapter(supportFragmentManager, lifecycle)

    }

    private inner class TabsAdapter(fa:FragmentManager,  lc: Lifecycle) : FragmentStateAdapter(fa,lc){
        override fun getItemCount(): Int = tabsList.size
        override fun createFragment(position: Int): Fragment = tabsList[position]
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeTab(url: String, fragment: Fragment){
        tabsList.add(fragment)
        binding.myPager.adapter?.notifyDataSetChanged()
        binding.myPager.currentItem = tabsList.size - 1
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBackPressed() {

        var frag:BrowseFragment? = null
        try {
            frag = tabsList[binding.myPager.currentItem] as BrowseFragment
        }catch (e:Exception){}

        when {
            frag?.binding?.webView?.canGoBack() == true -> frag.binding.webView.goBack()
            binding.myPager.currentItem != 0 ->{
                tabsList.removeAt(binding.myPager.currentItem)
                binding.myPager.adapter?.notifyDataSetChanged()
                binding.myPager.currentItem = tabsList.size - 1
            }
            else -> super.onBackPressed()
        }
    }

    fun onForwardPressed(){
        var frag:BrowseFragment? = null
        try {
            frag = tabsList[binding.myPager.currentItem] as BrowseFragment
        }catch (e:Exception){}
        frag?.apply {
            if(binding.webView.canGoForward())
                binding.webView.goForward()
        }
    }

}