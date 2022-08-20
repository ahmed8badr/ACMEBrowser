package com.example.acmebrowser.fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.acmebrowser.MainActivity
import com.example.acmebrowser.R
import com.example.acmebrowser.adapters.BookmarkAdapter
import com.example.acmebrowser.changeTab
import com.example.acmebrowser.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view  = inflater.inflate(R.layout.fragment_home, container, false)
        binding = FragmentHomeBinding.bind(view)

        return view
    }

    override fun onResume() {
        super.onResume()

        val mainActivityRef = requireActivity() as MainActivity

        MainActivity.tabsBtn.text = MainActivity.tabsList.size.toString()
        MainActivity.tabsList[MainActivity.myPager.currentItem].name = "Home"

        mainActivityRef.binding.inputUrl.setText("")
        binding.searchView.setQuery("",false)
        mainActivityRef.binding.logoIcon.setImageResource(R.drawable.ic_baseline_link_24)

        mainActivityRef.binding.reloadBtn.isClickable = false

        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(result: String?): Boolean {
                var link = result
                if(link!!.contains(".com",ignoreCase = true) && !link.contains("http")) { link = "http://$link"; }
                changeTab(link, BrowseFragment(link))
                return true
            }
            override fun onQueryTextChange(p0: String?): Boolean = false
        })

        mainActivityRef.binding.inputUrl.setOnEditorActionListener(object: TextView.OnEditorActionListener{
            override fun onEditorAction(textView: TextView?, i: Int, keyEvent: KeyEvent?): Boolean {
                if(i== EditorInfo.IME_ACTION_GO){
                    var link: String = mainActivityRef.binding.inputUrl.text.toString()
                    if(link.contains(".com", ignoreCase = true) && !link.contains("http")) { link = "http://$link" }
                    changeTab(link,
                        BrowseFragment(link))
                    return true
                }
                return false
            }

        })

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.setItemViewCacheSize(5)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 5)
        binding.recyclerView.adapter = BookmarkAdapter(requireContext())

    }
}