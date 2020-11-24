package com.github.sample.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class RepoItem(
    @SerializedName("name") val name: String?,
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("owner") val owner: RepoOwner?,
    @SerializedName("html_url") val htmlUrl: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("watchers_count") val watchersCount: Int,
    @SerializedName("forks_count") val forksCount: Int,
    @SerializedName("stargazers_count") val starsCount: Int,
    @SerializedName("id") val repoId: Int
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readParcelable<RepoOwner>(RepoOwner::class.java.classLoader),
        source.readString(),
        source.readString(),
        source.readInt(),
        source.readInt(),
        source.readInt(),
        source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeString(fullName)
        writeParcelable(owner, 0)
        writeString(htmlUrl)
        writeString(if (description == null || description.length == 0) "No Description" else description )
        writeInt(watchersCount)
        writeInt(forksCount)
        writeInt(starsCount)
        writeInt(repoId)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<RepoItem> = object : Parcelable.Creator<RepoItem> {
            override fun createFromParcel(source: Parcel): RepoItem = RepoItem(source)
            override fun newArray(size: Int): Array<RepoItem?> = arrayOfNulls(size)
        }
    }
}