package com.example.acmebrowser.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.acmebrowser.MainActivity
import com.example.acmebrowser.R
import com.example.acmebrowser.databinding.FragmentBrowseBinding
import java.io.ByteArrayOutputStream

class BrowseFragment(private var urlNew: String) : Fragment() {

    lateinit var binding: FragmentBrowseBinding
    var logoIcon: Bitmap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_browse, container, false)
        binding = FragmentBrowseBinding.bind(view)

        binding.webView.apply {
            when{
                URLUtil.isValidUrl(urlNew) -> loadUrl(urlNew)
                else -> loadUrl("https://www.google.com/search?q=$urlNew")
            }
        }

        return view
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onResume() {
        super.onResume()
        val mainActivityRef = requireActivity() as MainActivity
        mainActivityRef.binding.logoIcon.setImageResource(R.drawable.ic_baseline_link_24)

        MainActivity.tabsList[MainActivity.myPager.currentItem].name = binding.webView.url.toString()
        MainActivity.tabsBtn.text = MainActivity.tabsList.size.toString()

        mainActivityRef.binding.reloadBtn.isClickable = true
        mainActivityRef.binding.bookmarkBtn.isClickable = true

        mainActivityRef.binding.reloadBtn.setOnClickListener{
            binding.webView.reload()
        }

        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.setSupportZoom(true)
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            webViewClient = object: WebViewClient(){
                override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                    super.doUpdateVisitedHistory(view, url, isReload)
                    if (mainActivityRef.isBookmarked(binding.webView.url!!) != -1){
                        mainActivityRef.binding.bookmarkBtn.setImageResource(R.drawable.ic_baseline_bookmark_24)
                    } else mainActivityRef.binding.bookmarkBtn.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
                    mainActivityRef.binding.inputUrl.setText(url)
                    MainActivity.tabsList[MainActivity.myPager.currentItem].name = url.toString()
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    mainActivityRef.binding.progressBar.progress = 0
                    mainActivityRef.binding.progressBar.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    mainActivityRef.binding.progressBar.visibility = View.GONE
                    binding.webView.zoomOut()
                }
            }
            webChromeClient = object: WebChromeClient(){

                override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                    super.onReceivedIcon(view, icon)
                    try {
                        mainActivityRef.binding.logoIcon.setImageBitmap(icon)
                        logoIcon = icon
                        MainActivity.bookmarkIndex = mainActivityRef.isBookmarked(view?.url!!)
                        if(MainActivity.bookmarkIndex != -1){
                            val array = ByteArrayOutputStream()
                            icon!!.compress(Bitmap.CompressFormat.PNG, 100, array)
                            MainActivity.bookmarkList[MainActivity.bookmarkIndex].image = array.toByteArray()
                        }
                    }catch (e:Exception){}
                }

                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    mainActivityRef.binding.progressBar.progress = newProgress
                }
            }
            binding.webView.reload()
        }
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as MainActivity).setBookmarks()
    }
}