package com.github.sample.main

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.sample.webservice.GITConstants
import com.github.sample.R
import com.github.sample.canvas.CanvasActivity
import com.github.sample.models.RepoItem
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), MainView, MainAdapter.OnItemClickListener,
    MainAdapter.OnLoadMoreListener {

    private lateinit var mainPresenterImpl: MainPresenterImpl
    private lateinit var repoAdapter: MainAdapter
    private lateinit var searchView: SearchView
    private lateinit var mHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        mainPresenterImpl = MainPresenterImpl(this)

        initRecyclerView()
        mHandler = Handler()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem: MenuItem? = menu?.findItem(R.id.action_search)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = searchItem?.actionView as SearchView

        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView?.queryHint = getString(R.string.search_hint)
        var searchText = ""
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mHandler.removeCallbacksAndMessages(null)
                mHandler.postDelayed(Runnable {
                    if (query != null && query.length >= 3 && !query.equals(searchText)) {
                        searchText = query
                        mainPresenterImpl.searchRepos(query)
                    }
                }, 300)
                searchView.onActionViewExpanded()
                hideKeyboard(this@MainActivity)
                return true
            }


            override fun onQueryTextChange(newText: String?): Boolean {
                mHandler.removeCallbacksAndMessages(null)
                mHandler.postDelayed(Runnable {
                    if (newText != null && newText.length >= 3) {
                        searchText = newText
                        mainPresenterImpl.searchRepos(newText)
                    }
                }, 300)

                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_canvas) {
            this.startActivity(Intent(this, CanvasActivity::class.java))
            return true
        } else if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initRecyclerView() {
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = LinearLayoutManager(this)

        repoAdapter = MainAdapter(true, recyclerView)
        repoAdapter.onItemClickListener = this
        repoAdapter.onLoadMoreListener = this

        recyclerView.adapter = repoAdapter
    }

    override fun onItemClick(item: RepoItem) {
        val intent = Intent(this, DetailedActivity::class.java)
        intent.putExtra(GITConstants.GIT_REPO_DETAILS, item)
        startActivity(intent)
    }

    override fun onLoadMore() {
        mainPresenterImpl.onLoadMore()
    }

    override fun changeMessage(message: String) {
        textViewMessage.text = message
        showMessage()
    }

    override fun showMessage() {
        hideList()
        textViewMessage.visibility = View.VISIBLE
    }

    override fun hideMessage() {
        showList()
        textViewMessage.visibility = View.GONE
    }

    override fun updateList(items: List<RepoItem>) {
        hideMessage()
        repoAdapter.setLoaded()
        repoAdapter.updateItems(items)
    }

    override fun clearList() {
        repoAdapter.clearItems()
    }

    override fun showList() {
        recyclerView.visibility = View.VISIBLE
    }

    override fun hideList() {
        recyclerView.visibility = View.GONE
    }

    override fun onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.onActionViewCollapsed();
        } else {
            super.onBackPressed();
        }
    }

    fun hideKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


}