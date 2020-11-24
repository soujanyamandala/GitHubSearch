package com.github.sample.webservice

import com.github.sample.models.ReposList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET(GITConstants.GIT_REPOS_LIST)
    fun getRepos(@Query("q") q: String,
                 @Query("per_page") per_page: Int,
                 @Query("page") page: Int): Call<ReposList>

}