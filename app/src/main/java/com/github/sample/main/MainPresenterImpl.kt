package com.github.sample.main

import android.app.Activity
import android.content.Context
import android.util.Log
import com.github.sample.GithubApplication
import com.github.sample.R
import com.github.sample.models.ReposList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class MainPresenterImpl(private var context: Context) : MainPresenter {

    private var homeView: MainView = context as MainView

    private var searchText: String = ""
    private var sortBy: String = ""
    private var orderBy: String = ""
    private var pageNum: Int = 0
    private var pageLength: Int = 10
    private var itemsCount: Int = 0
    private var totalCount: Int = 0

    private var isFilterApplied: Boolean = false

    private fun resetData() {
        homeView.clearList()

        pageLength = 10
        pageNum= 1

        totalCount = 0
        itemsCount = 0
    }

    private fun clearFilters() {
        orderBy = ""
        sortBy = ""
    }

    private fun onSearching() {
        homeView.changeMessage(context.getString(R.string.fetching))
    }

    private fun showNoReposFound() {
        if (itemsCount == 0) homeView.changeMessage(context.getString(R.string.no_repo_found))
    }

    private fun manageResponse(response: Response<ReposList>) {
        try {
            totalCount = response.body()!!.totalCount
            itemsCount += response.body()!!.items.size

            showNoReposFound()

            if (itemsCount != 0) {
                homeView.updateList(response.body()!!.items)
            }
        }catch (e: Exception) {
            Log.e("manageResponse", e.toString())
        }
    }

    private fun getReposService() {
        GithubApplication.service.getRepos(searchText, pageLength, pageNum).enqueue(object: Callback<ReposList> {
            override fun onFailure(call: Call<ReposList>, t: Throwable) {
                showNoReposFound()
            }

            override fun onResponse(call: Call<ReposList>, response: Response<ReposList>) {
                manageResponse(response)
            }

        })
    }

    override fun searchRepos(searchText: String) {
        if (this.searchText != searchText) {
            resetData()
        }

        this.searchText = searchText

        if (pageNum == 1) {
            onSearching()
        }

        getReposService()
    }

    override fun onDialogCancel() {
        if (isFilterApplied) {
            getReposService()
        }
    }

    override fun onLoadMore() {
        if (itemsCount != 0 && totalCount != itemsCount) {
            pageNum++
            getReposService()
        }
    }
}