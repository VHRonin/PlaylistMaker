package com.example.playlistmaker.ui.search.fragment

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.search.SearchResult
import com.example.playlistmaker.ui.search.TrackAdapter
import com.example.playlistmaker.ui.search.view_model.SearchState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {
    companion object{
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val TEXT = ""
    }

    private lateinit var tracksAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        viewModel.observeState().observe(viewLifecycleOwner){
            tracksAdapter.tracks = it.tracks
            tracksAdapter.notifyDataSetChanged()

            historyAdapter.tracks = it.historyTracks
            historyAdapter.notifyDataSetChanged()

            when(it.searchState){
                is SearchState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is SearchState.Result -> {
                    checkResponse((it.searchState as SearchState.Result).searchResult)
                    binding.progressBar.visibility = View.GONE
                }
                is SearchState.Default -> binding.progressBar.visibility = View.GONE
            }
        }

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.searchToolBar)

        binding.clearButton.setOnClickListener {
            binding.searchEditText.setText("")

            val imm = (requireActivity() as AppCompatActivity).getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

            imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)

            binding.searchEditText.clearFocus()
            viewModel.clearSearch()
            if (binding.connectionContainer.visibility == View.VISIBLE) clearMessageVisibility()
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
                binding.clearButton.visibility = buttonVisibility(s)
                viewModel.addTextToQuery(s.toString())

                if (binding.searchEditText.text.isEmpty()){
                    viewModel.clearSearch()
                    clearMessageVisibility()
                }

                binding.hintMessageHistory.visibility = if (binding.searchEditText.hasFocus() && s?.isEmpty() == true && historyAdapter.tracks.isNotEmpty()) View.VISIBLE else View.GONE
                viewModel.getTrackHistory()

                viewModel.searchDebounce()
                clearMessageVisibility()
            }

        }

        binding.apply {
            searchEditText.addTextChangedListener(searchTextWatcher)

            tracksRecyclerView.adapter = tracksAdapter
            historyRecyclerView.adapter = historyAdapter

            connectionButton.setOnClickListener {
                clearMessageVisibility()
                viewModel.retrySearch()
            }

            searchEditText.setOnFocusChangeListener {view, hasFocus ->
                hintMessageHistory.visibility = if (hasFocus && searchEditText.text.isEmpty() && historyAdapter.tracks.isNotEmpty()) View.VISIBLE else View.GONE
                viewModel.getTrackHistory()
            }

            clearHistoryButton.setOnClickListener {
                viewModel.clearHistory()
                hintMessageHistory.visibility = View.GONE
            }
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
        binding.apply {
            connectionContainer.visibility = View.VISIBLE
            connectionIcon.setImageResource(R.drawable.ic_not_found)
            connectionText.text = getString(R.string.no_found)
            connectionButton.visibility = View.GONE
        }
    }

    private fun showInternetErrorMessage(){
        binding.apply {
            connectionContainer.visibility = View.VISIBLE
            connectionIcon.setImageResource(R.drawable.ic_no_internet)
            connectionText.text = getString(R.string.no_internet)
            connectionButton.visibility = View.VISIBLE
        }
    }

    private fun clearMessageVisibility(){
        binding.apply {
            connectionContainer.visibility = View.GONE
            connectionButton.visibility = View.GONE
        }
    }

    private fun buttonVisibility(s: CharSequence?) = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
}