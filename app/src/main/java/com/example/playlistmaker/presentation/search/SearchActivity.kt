package com.example.playlistmaker.presentation.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.data.dto.TracksResponse
import com.example.playlistmaker.domain.SearchResult
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.tracks.SearchHistory
import com.example.playlistmaker.domain.models.Track
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var searchString: String = TEXT
    private var lastFailedTerm = ""

    private lateinit var searchEditText: EditText
    private lateinit var tracksRecyclerView: RecyclerView

    private lateinit var connectionContainer: LinearLayout
    private lateinit var connectionIcon: ImageView
    private lateinit var connectionText: TextView
    private lateinit var connectionButton: Button

    private lateinit var hintMessage: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: ProgressBar

    private val tracks = ArrayList<Track>()
    private val tracksAdapter = TrackAdapter({ debounceClick() })
    private val historyAdapter = TrackAdapter({ debounceClick() })
    private lateinit var searchHistory: SearchHistory

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable{findTracks(searchString)}


    private lateinit var tracksInteractor: TracksInteractor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences(SEARCH_PREFERENCES, MODE_PRIVATE)

        searchEditText = findViewById<EditText>(R.id.searchEditText)
        val clearButton = findViewById<ImageButton>(R.id.clearButton)
        val searchToolBar = findViewById<MaterialToolbar>(R.id.searchToolBar)
        tracksRecyclerView = findViewById<RecyclerView>(R.id.tracksRecyclerView)

        connectionContainer = findViewById(R.id.connectionContainer)
        connectionIcon = findViewById(R.id.connectionIcon)
        connectionText = findViewById(R.id.connectionText)
        connectionButton = findViewById(R.id.connectionButton)

        hintMessage = findViewById(R.id.hintMessageHistory)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        progressBar = findViewById(R.id.progressBar)

        searchHistory = SearchHistory(sharedPreferences)

        tracksAdapter.searchHistory = searchHistory
        historyAdapter.searchHistory = searchHistory


        historyAdapter.onClick = { historyAdapter.notifyDataSetChanged() }

        setSupportActionBar(searchToolBar)

        searchToolBar.setNavigationOnClickListener { finish() }

        clearButton.setOnClickListener {
            searchEditText.setText("")

            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)

            searchEditText.clearFocus()

            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            if (connectionContainer.visibility == View.VISIBLE) clearMessageVisibility()
        }

        val searchTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                clearButton.visibility = buttonVisibility(s)
                searchString = s.toString()

                if (searchEditText.text.isEmpty()){
                    clearTracks()
                    clearMessageVisibility()
                }

                hintMessage.visibility = if (searchEditText.hasFocus() && s?.isEmpty() == true && searchHistory.getHistory().isNotEmpty()) View.VISIBLE else View.GONE
                searchHistory.fillTracksHistory()
                historyAdapter.notifyDataSetChanged()

                searchDebounce()
            }

        }

        tracksAdapter.tracks = tracks
        historyAdapter.tracks = searchHistory.tracks

        searchEditText.addTextChangedListener(searchTextWatcher)

        tracksRecyclerView.adapter = tracksAdapter
        historyRecyclerView.adapter = historyAdapter

        connectionButton.setOnClickListener { findTracks(lastFailedTerm) }

        searchEditText.setOnFocusChangeListener {view, hasFocus ->
            hintMessage.visibility = if (hasFocus && searchEditText.text.isEmpty() && searchHistory.getHistory().isNotEmpty()) View.VISIBLE else View.GONE
            searchHistory.fillTracksHistory()
            historyAdapter.notifyDataSetChanged()
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clear()
            historyAdapter.notifyDataSetChanged()
            hintMessage.visibility = View.GONE
        }

        tracksInteractor = Creator.provideTrackInteractor()
    }

    private fun findTracks(text: String){
        if (text.isNotEmpty()){
            progressBar.visibility = View.VISIBLE
            clearTracks()

            tracksInteractor.searchTracks(
                text,
                object : TracksInteractor.TracksConsumer{
                    override fun consume(searchResult: SearchResult) {
                        Handler(Looper.getMainLooper()).post {
                            progressBar.visibility = View.GONE
                            when (searchResult){
                                is SearchResult.Success -> {
                                    tracks.clear()

                                    tracks.addAll(searchResult.foundTracks)
                                    tracksAdapter.notifyDataSetChanged()

                                    checkResponse(searchResult.code)
                                }
                                is SearchResult.NothingFound -> {
                                    checkResponse(searchResult.code)
                                }
                                else -> {
                                    checkResponse((searchResult as SearchResult.NetworkError).code)
                                }
                            }
                        }
                    }
                }
            )
        }
    }

    private fun checkResponse(code: Int){
        when (code){
            200 -> {
                if (tracks.isEmpty()){
                    clearTracks()
                    showNotFoundMessage()
                }
                else{
                    clearMessageVisibility()
                }
            }
            else -> {
                clearTracks()
                showInternetErrorMessage()
            }
        }
    }

    private fun showNotFoundMessage(){
        connectionContainer.visibility = View.VISIBLE
        connectionIcon.setImageResource(R.drawable.ic_not_found)
        connectionText.text = getString(R.string.no_found)
        connectionButton.visibility = View.GONE
    }

    private fun showInternetErrorMessage(){
        connectionContainer.visibility = View.VISIBLE
        connectionIcon.setImageResource(R.drawable.ic_no_internet)
        connectionText.text = getString(R.string.no_internet)
        connectionButton.visibility = View.VISIBLE

        lastFailedTerm = searchEditText.text.toString()
    }

    private fun clearMessageVisibility(){
        connectionContainer.visibility = View.GONE
        connectionButton.visibility = View.GONE
    }

    private fun clearTracks(){
        tracks.clear()
        tracksAdapter.notifyDataSetChanged()
    }

    private fun buttonVisibility(s: CharSequence?) = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

    private fun debounceClick(): Boolean{
        val current = isClickAllowed
        if (isClickAllowed){
            isClickAllowed = false
            handler.postDelayed({isClickAllowed = true}, CLICK_DEBOUNCE_DELAY)
        }

        return current
    }

    private fun searchDebounce(){
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchString)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchString = savedInstanceState.getString(SEARCH_TEXT, TEXT)

        val searchEditText = findViewById<EditText>(R.id.searchEditText)

        searchEditText.setText(searchString)
        searchEditText.setSelection(searchEditText.text.length)
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val TEXT = ""
        const val SEARCH_PREFERENCES = "search_preferences"
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

}