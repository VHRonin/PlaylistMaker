package com.example.playlistmaker

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.network.ITunesApi
import com.example.playlistmaker.network.TracksResponse
import com.example.playlistmaker.tracks.Track
import com.example.playlistmaker.tracks.TrackAdapter
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class SearchActivity : AppCompatActivity() {

    private var searchString: String = TEXT
    private var lastFailedTerm = ""

    private lateinit var searchEditText: EditText
    private lateinit var tracksRecyclerView: RecyclerView

    private lateinit var connectionContainer: LinearLayout
    private lateinit var connectionIcon: ImageView
    private lateinit var connectionText: TextView
    private lateinit var connectionButton: Button

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)

    private val tracks = ArrayList<Track>()
    private val tracksAdapter = TrackAdapter()

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

        setSupportActionBar(searchToolBar)

        searchToolBar.setNavigationOnClickListener { finish() }

        clearButton.setOnClickListener {
            searchEditText.setText("")

            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)

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
            }

        }

        tracksAdapter.tracks = tracks

        searchEditText.addTextChangedListener(searchTextWatcher)
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                findTracks(searchEditText.text.toString())
                true
            }
            else{
                false
            }
        }

        tracksRecyclerView.adapter = tracksAdapter

        connectionButton.setOnClickListener { findTracks(lastFailedTerm) }
    }

    private fun findTracks(text: String){
        if (text.isNotEmpty()){
            iTunesService.search(text).enqueue(object : Callback<TracksResponse>{
                override fun onResponse(
                    call: Call<TracksResponse?>,
                    response: Response<TracksResponse?>
                ) {
                    if (response.code() == 200){
                        tracks.clear()

                        if (response.body()?.results?.isNotEmpty() == true){
                            tracks.addAll(response.body()?.results!!)
                            tracksAdapter.notifyDataSetChanged()
                        }
                        checkResponse(response.code())
                    }
                    else{
                        checkResponse(response.code())
                    }
                }

                override fun onFailure(
                    call: Call<TracksResponse?>,
                    t: Throwable
                ) {
                    checkResponse(-1)
                }


            })
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchString)
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
    }

}