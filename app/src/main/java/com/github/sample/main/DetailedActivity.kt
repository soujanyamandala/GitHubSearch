package com.github.sample.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.github.sample.webservice.GITConstants
import com.github.sample.R
import com.github.sample.canvas.CanvasActivity
import com.github.sample.models.RepoItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_repo.*

class DetailedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val item: RepoItem = intent.getParcelableExtra(GITConstants.GIT_REPO_DETAILS) as RepoItem

        textViewName.text = item.name
        textViewLink.text = item.htmlUrl
        textViewDesc.text = item.description
        textViewForks.text = item.forksCount.toString()
        textViewStars.text = item.starsCount.toString()

        Picasso.get().load(item.owner?.avatarUrl)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .into(imageViewProfile)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
