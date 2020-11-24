package com.github.sample.canvas

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.github.sample.R
import com.github.sample.main.MainActivity
import kotlinx.android.synthetic.main.canvas_playground.*

class CanvasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.canvas_playground)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupUI()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            this.startActivity(Intent(this, MainActivity::class.java))
        return super.onOptionsItemSelected(item)
    }

    private fun setupUI() {
        addButton.setOnClickListener {
            canvasPlaygroundView.add()
        }
        deleteButton.setOnClickListener {
            canvasPlaygroundView.delete()
        }
    }
}