package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.tracks.dpToPx
import com.example.playlistmaker.tracks.formatTime
import com.google.android.material.appbar.MaterialToolbar

class PlayerActivity : AppCompatActivity() {
    private lateinit var artwork: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var collectionName: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView

    private lateinit var collectionNameGroup: Group
    private lateinit var releaseDateGroup: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolBar = findViewById<MaterialToolbar>(R.id.playerToolBar)

        setSupportActionBar(toolBar)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolBar.setNavigationOnClickListener { finish() }

        initValues()
        checkValues()
    }

    private fun initValues(){
        artwork = findViewById(R.id.artwork)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        trackTime = findViewById(R.id.trackTimeValue)
        collectionName = findViewById(R.id.collectionNameValue)
        releaseDate = findViewById(R.id.releaseDateValue)
        primaryGenreName = findViewById(R.id.primaryGenreNameValue)
        country = findViewById(R.id.countryValue)

        val roundedCorners = dpToPx(8f, this)

        Glide
            .with(this)
            .load(intent.getStringExtra("artwork"))
            .placeholder(R.drawable.ic_placeholder_track)
            .error(R.drawable.ic_placeholder_track)
            .centerCrop()
            .transform(RoundedCorners(roundedCorners))
            .into(artwork)

        trackName.text = intent.getStringExtra("trackName")
        artistName.text = intent.getStringExtra("artistName")
        trackTime.text = intent.getStringExtra("trackTime")?.let { formatTime(it) } ?: "--:--"
        collectionName.text = intent.getStringExtra("collectionName")
        releaseDate.text = intent.getStringExtra("releaseDate")?.take(4)
        primaryGenreName.text = intent.getStringExtra("primaryGenreName")
        country.text = intent.getStringExtra("country")
    }

    private fun checkValues(){
        collectionNameGroup = findViewById(R.id.collectionNameGroup)
        releaseDateGroup = findViewById(R.id.releaseDateGroup)

        if (collectionName.text.isEmpty()) collectionNameGroup.visibility = View.GONE
        if (releaseDate.text.isEmpty()) releaseDateGroup.visibility = View.GONE
    }
}