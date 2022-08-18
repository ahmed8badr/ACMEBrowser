package com.example.acmebrowser

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
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
        mainActivityRef.binding.inputUrl.setText("")
        binding.searchView.setQuery("",false)
        mainActivityRef.binding.logoIcon.setImageResource(R.drawable.ic_baseline_link_24)

        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(result: String?): Boolean {
                mainActivityRef.changeTab(result!!, BrowseFragment("https://www.google.com/search?q=$result"))
                return true
            }
            override fun onQueryTextChange(p0: String?): Boolean = false
        })

        mainActivityRef.binding.clearBtn.setOnClickListener{
            mainActivityRef.binding.inputUrl.setText("")
        }

        mainActivityRef.binding.backBtn.setOnClickListener{
            mainActivityRef.onBackPressed()
        }

        mainActivityRef.binding.forwardBtn.setOnClickListener{
            mainActivityRef.onForwardPressed()
        }

        mainActivityRef.binding.reloadBtn.isClickable = false

        mainActivityRef.binding.inputUrl.setOnEditorActionListener(object: TextView.OnEditorActionListener{
            override fun onEditorAction(textView: TextView?, i: Int, keyEvent: KeyEvent?): Boolean {
                if(i==EditorInfo.IME_ACTION_GO || i==EditorInfo.IME_ACTION_DONE){
                    if(mainActivityRef.binding.inputUrl.text.toString().contains(".com",ignoreCase = true))
                    mainActivityRef.changeTab(mainActivityRef.binding.inputUrl.text.toString(),BrowseFragment("https://" + mainActivityRef.binding.inputUrl.text.toString()))
                    else mainActivityRef.changeTab(mainActivityRef.binding.inputUrl.text.toString(),BrowseFragment("https://www.google.com/search?q=" + mainActivityRef.binding.inputUrl.text.toString()))
                    return true
                }
                return false
            }

        })

    }
}