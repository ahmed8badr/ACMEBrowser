package com.example.acmebrowser

import android.annotation.SuppressLint
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
import com.example.acmebrowser.databinding.ActivityMainBinding
import com.example.acmebrowser.databinding.TabsWindowBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    companion object{
        var tabsList: ArrayList<Tab> = ArrayList()
        lateinit var myPager: ViewPager2
        lateinit var tabsBtn: MaterialTextView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        tabsList.add(Tab("Home",HomeFragment()))

        binding.myPager.isUserInputEnabled = false
        binding.myPager.adapter = TabsAdapter(supportFragmentManager, lifecycle)
        myPager = binding.myPager
        tabsBtn = binding.tabsBtn

        initializeView()

    }

    private fun initializeView(){
        binding.tabsBtn.setOnClickListener{
            val viewTabs = layoutInflater.inflate(R.layout.tabs_window, binding.root,false)
            val bindingTabs = TabsWindowBinding.bind(viewTabs)
            val dialogTabs = MaterialAlertDialogBuilder(this, R.style.roundCornerDialog).setView(viewTabs)
                .setTitle("Select Tab")
                .setPositiveButton("new tab"){self,_->
                    changeTab("Home",HomeFragment())
                    self.dismiss()}
                .create()

            bindingTabs.tabsRV.setHasFixedSize(true)
            bindingTabs.tabsRV.layoutManager = LinearLayoutManager(this)
            bindingTabs.tabsRV.adapter = TabAdapter(this,dialogTabs)

            dialogTabs.show()
            val pBtn = dialogTabs.getButton(AlertDialog.BUTTON_POSITIVE)

            pBtn.isAllCaps = false
            pBtn.setTextColor(Color.BLACK)
            pBtn.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(resources,R.drawable.ic_baseline_add_24,theme)
                ,null,null,null)
        }
    }

    private inner class TabsAdapter(fa:FragmentManager,  lc: Lifecycle) : FragmentStateAdapter(fa,lc){
        override fun getItemCount(): Int = tabsList.size
        override fun createFragment(position: Int): Fragment = tabsList[position].fragment
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeTab(url: String, fragment: Fragment){
        tabsList.add(Tab(url,fragment))
        myPager.adapter?.notifyDataSetChanged()
        myPager.currentItem = tabsList.size - 1
        tabsBtn.text = tabsList.size.toString()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBackPressed() {

        var frag:BrowseFragment? = null
        try {
            frag = tabsList[binding.myPager.currentItem].fragment as BrowseFragment
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
            frag = tabsList[binding.myPager.currentItem].fragment as BrowseFragment
        }catch (e:Exception){}
        frag?.apply {
            if(binding.webView.canGoForward())
                binding.webView.goForward()
        }
    }

}