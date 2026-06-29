package com.example.playlistmaker.ui.player.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.player.PlayerState
import com.example.playlistmaker.domain.player.api.PlayerInteractor
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.search.dpToPx
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Runnable

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
    private lateinit var playerButton: ImageButton
    private lateinit var trackCurrentTime: TextView
    private lateinit var previewUrl: String
    private lateinit var viewModel: PlayerViewModel

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

        viewModel.observeState().observe(this){
            when(it.playerState){
                is PlayerState.Paused, is PlayerState.Prepared -> playerButton.setImageResource(R.drawable.ic_play_button)
                is PlayerState.Playing, is PlayerState.Default -> playerButton.setImageResource(R.drawable.ic_stop_button)
            }

            trackCurrentTime.text = it.trackTimer
        }

        viewModel.preparePlayer(previewUrl)

        playerButton.setOnClickListener {
            viewModel.handlePlayButton()
        }
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
        playerButton = findViewById(R.id.playerButton)
        trackCurrentTime = findViewById(R.id.trackCurrentTime)

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
        trackTime.text = intent.getStringExtra("trackTime") ?: "--:--"
        collectionName.text = intent.getStringExtra("collectionName")
        releaseDate.text = intent.getStringExtra("releaseDate")?.take(4)
        primaryGenreName.text = intent.getStringExtra("primaryGenreName")
        country.text = intent.getStringExtra("country")
        previewUrl = intent.getStringExtra("previewUrl") ?: ""

        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getFactory()
        ).get(PlayerViewModel::class.java)
    }

    private fun checkValues(){
        collectionNameGroup = findViewById(R.id.collectionNameGroup)
        releaseDateGroup = findViewById(R.id.releaseDateGroup)

        if (collectionName.text.isEmpty()) collectionNameGroup.visibility = View.GONE
        if (releaseDate.text.isEmpty()) releaseDateGroup.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }
}