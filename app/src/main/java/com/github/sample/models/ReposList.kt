package com.github.sample.models

import com.google.gson.annotations.SerializedName

data class ReposList(
        @SerializedName("total_count") val totalCount: Int,
        @SerializedName("incomplete_results") val incompleteResults: Boolean,
        @SerializedName("items") val items: List<RepoItem>
)