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
        val searchBack = findViewById<ImageView>(R.id.searchBack)

        searchBack.setOnClickListener { finish() }

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