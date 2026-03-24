package com.example.playlistmaker

import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.tracks.Track
import com.example.playlistmaker.tracks.TrackAdapter
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {

    private var searchString: String = TEXT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val clearButton = findViewById<ImageButton>(R.id.clearButton)
        val searchToolBar = findViewById<MaterialToolbar>(R.id.searchToolBar)
        val tracksRecyclerView = findViewById<RecyclerView>(R.id.tracksRecyclerView)

        setSupportActionBar(searchToolBar)

        searchToolBar.setNavigationOnClickListener { finish() }

        clearButton.setOnClickListener {
            searchEditText.setText("")

            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
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
            }

        }

        searchEditText.addTextChangedListener(searchTextWatcher)

        val tracksAdapter = TrackAdapter(
            listOf(
                Track(
                    "Smells Like Teen Spirit",
                    "Nirvana",
                    "5:01",
                    "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
                ),
                Track(
                    "Billie Jean",
                    "Michael Jackson",
                    "4:35",
                    "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
                ),
                Track(
                    "Stayin' Alive",
                    "Bee Gees",
                    "4:10",
                    "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
                ),
                Track(
                    "Whole Lotta Love",
                    "Led Zeppelin",
                    "5:33",
                    "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
                ),
                Track(
                    "Sweet Child O'Mine",
                    "Guns N' Roses",
                    "5:03",
                    "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
                )
            )
        )

        tracksRecyclerView.adapter = tracksAdapter
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