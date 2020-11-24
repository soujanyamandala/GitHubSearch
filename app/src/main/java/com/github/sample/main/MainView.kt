package com.github.sample.main

import com.github.sample.models.RepoItem


interface MainView {

    fun changeMessage(message: String)

    fun showMessage()

    fun hideMessage()

    fun updateList(items: List<RepoItem>)

    fun clearList()

    fun showList()

    fun hideList()

}