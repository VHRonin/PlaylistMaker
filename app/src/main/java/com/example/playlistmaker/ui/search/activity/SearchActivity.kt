package com.example.playlistmaker.ui.search.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.domain.search.SearchResult
import com.example.playlistmaker.domain.search.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.api.TracksInteractor
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.ui.search.TrackAdapter
import com.example.playlistmaker.ui.search.view_model.SearchState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import com.google.android.material.appbar.MaterialToolbar
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.startKoin

class SearchActivity : AppCompatActivity() {
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
    private lateinit var tracksAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private val viewModel by viewModel<SearchViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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

        tracksAdapter = TrackAdapter(
            debounceClick = { viewModel.debounceClick() },
            onAddToHistoryClick = {track ->
                viewModel.addTrackToHistory(track)
            }
        )
        historyAdapter = TrackAdapter(
            debounceClick = { viewModel.debounceClick() },
            onAddToHistoryClick = {track ->
                viewModel.addTrackToHistory(track)
            }
        )

        viewModel.observeState().observe(this){
            tracksAdapter.tracks = it.tracks
            tracksAdapter.notifyDataSetChanged()

            historyAdapter.tracks = it.historyTracks
            historyAdapter.notifyDataSetChanged()

            when(it.searchState){
                is SearchState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is SearchState.Result -> {
                    checkResponse((it.searchState as SearchState.Result).searchResult)
                    progressBar.visibility = View.GONE
                }
                is SearchState.Default -> progressBar.visibility = View.GONE
            }
        }


//        historyAdapter.onClick = {
//            viewModel.getTrackHistory()
//        }

        setSupportActionBar(searchToolBar)

        searchToolBar.setNavigationOnClickListener { finish() }

        clearButton.setOnClickListener {
            searchEditText.setText("")

            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)

            searchEditText.clearFocus()
            viewModel.clearSearch()
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
                viewModel.addTextToQuery(s.toString())

                if (searchEditText.text.isEmpty()){
                    viewModel.clearSearch()
                    clearMessageVisibility()
                }

                hintMessage.visibility = if (searchEditText.hasFocus() && s?.isEmpty() == true && historyAdapter.tracks.isNotEmpty()) View.VISIBLE else View.GONE
                viewModel.getTrackHistory()

                viewModel.searchDebounce()
                clearMessageVisibility()
            }

        }

        searchEditText.addTextChangedListener(searchTextWatcher)

        tracksRecyclerView.adapter = tracksAdapter
        historyRecyclerView.adapter = historyAdapter

        connectionButton.setOnClickListener {
            clearMessageVisibility()
            viewModel.retrySearch()
        }

        searchEditText.setOnFocusChangeListener {view, hasFocus ->
            hintMessage.visibility = if (hasFocus && searchEditText.text.isEmpty() && historyAdapter.tracks.isNotEmpty()) View.VISIBLE else View.GONE
            viewModel.getTrackHistory()
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
            hintMessage.visibility = View.GONE
        }
    }

    private fun checkResponse(searchResult: SearchResult){
        when (searchResult){
            is SearchResult.Success -> {
                clearMessageVisibility()
            }
            is SearchResult.NothingFound -> {
                showNotFoundMessage()
            }
            else -> {
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
    }

    private fun clearMessageVisibility(){
        connectionContainer.visibility = View.GONE
        connectionButton.visibility = View.GONE
    }

    private fun buttonVisibility(s: CharSequence?) = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchEditText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val searchString = savedInstanceState.getString(SEARCH_TEXT, TEXT)

        val searchEditText = findViewById<EditText>(R.id.searchEditText)

        searchEditText.setText(searchString)
        searchEditText.setSelection(searchEditText.text.length)
    }

    companion object{
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val TEXT = ""
    }

}