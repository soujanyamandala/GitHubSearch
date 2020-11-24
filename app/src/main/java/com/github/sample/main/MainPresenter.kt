package com.github.sample.main

interface MainPresenter {

    fun searchRepos(searchText: String);

    fun onDialogCancel()

    fun onLoadMore()
}