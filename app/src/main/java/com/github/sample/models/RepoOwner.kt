package com.github.sample.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class RepoOwner(
    @SerializedName("login") val login: String?,
    @SerializedName("id") val id: Int,
    @SerializedName("node_id") val nodeId: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("html_url") val htmlUrl: String?
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readInt(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(login)
        writeInt(id)
        writeString(nodeId)
        writeString(avatarUrl)
        writeString(url)
        writeString(htmlUrl)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<RepoOwner> = object : Parcelable.Creator<RepoOwner> {
            override fun createFromParcel(source: Parcel): RepoOwner = RepoOwner(source)
            override fun newArray(size: Int): Array<RepoOwner?> = arrayOfNulls(size)
        }
    }
}