package com.example.playlistmaker.presentation.player

import android.media.MediaPlayer
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.PlayerState
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.presentation.search.dpToPx
import com.example.playlistmaker.presentation.search.formatTime
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Runnable
import java.text.SimpleDateFormat
import java.util.Locale

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

    private lateinit var playerInteractor: PlayerInteractor

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var mediaRunnable: Runnable

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

        preparePlayer()
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
        trackTime.text = intent.getStringExtra("trackTime")?.let { formatTime(it) } ?: "--:--"
        collectionName.text = intent.getStringExtra("collectionName")
        releaseDate.text = intent.getStringExtra("releaseDate")?.take(4)
        primaryGenreName.text = intent.getStringExtra("primaryGenreName")
        country.text = intent.getStringExtra("country")
        previewUrl = intent.getStringExtra("previewUrl") ?: ""

        playerInteractor = Creator.providePlayerInteractor()
    }

    private fun checkValues(){
        collectionNameGroup = findViewById(R.id.collectionNameGroup)
        releaseDateGroup = findViewById(R.id.releaseDateGroup)

        if (collectionName.text.isEmpty()) collectionNameGroup.visibility = View.GONE
        if (releaseDate.text.isEmpty()) releaseDateGroup.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.releasePlayer()
    }

    private fun handlePlayButton(){
        when (playerInteractor.getPlayerState()){
            is PlayerState.Playing -> pausePlayer()
            is PlayerState.Paused, is PlayerState.Prepared -> startPlayer()
            is PlayerState.Default -> {}
        }
    }

    private fun preparePlayer(){
        playerInteractor.preparePlayer(previewUrl){
            playerButton.setImageResource(R.drawable.ic_play_button)
        }

        playerButton.setOnClickListener {
            handlePlayButton()
        }
    }

    private fun startPlayer(){
        playerInteractor.startPlayer {
            playerButton.setImageResource(R.drawable.ic_stop_button)
        }

        mediaRunnable = createPlayerRunnable()
        handler.post(mediaRunnable)
    }

    private fun pausePlayer(){
        playerInteractor.pausePlayer {
            playerButton.setImageResource(R.drawable.ic_play_button)
        }
    }
    private fun createPlayerRunnable(): Runnable {
        return object: Runnable {
            override fun run() {
                when (playerInteractor.getPlayerState()){
                    is PlayerState.Playing -> {
                        trackCurrentTime.text = playerInteractor.getCurrentTIme()
                        handler.postDelayed(this, TRACK_TIME_DELAY)
                    }
                    is PlayerState.Paused -> {
                        handler.removeCallbacks(this)
                    }
                    else -> {}
                }
            }
        }
    }

    companion object{
        const val TRACK_TIME_DELAY = 300L
    }
}