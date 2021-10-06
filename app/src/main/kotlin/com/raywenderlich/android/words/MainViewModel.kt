package com.raywenderlich.android.words

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.raywenderlich.android.words.data.words.RandomWords
import com.raywenderlich.android.words.data.words.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class MainViewModel(application: Application) : AndroidViewModel(application) {
  private val _isLoading = MutableStateFlow(true)
  val isLoading: StateFlow<Boolean> = _isLoading

  // val words: List<Word> = RandomWords.map { Word(value = it) }
  private val wordRepository = getApplication<WordsApp>().wordRepository
  //  private val _words = MutableStateFlow(emptyFlow<PagingData<Word>>()) // empty mutable state flow
  //  val words: StateFlow<Flow<PagingData<Word>>> = _words // mutable state flow exposed as non mutable state flow

  // two separate MutableStateFlow properties: one for all words and another for searched words
  private val allWords = MutableStateFlow(emptyFlow<PagingData<Word>>())
  private val searchWords = MutableStateFlow(emptyFlow<PagingData<Word>>())

  // search
  private val _search = MutableStateFlow(null as String?)
  val search: StateFlow<String?> = _search

  @OptIn(ExperimentalCoroutinesApi::class)
  val words: StateFlow<Flow<PagingData<Word>>> =
    search
      .flatMapLatest { search -> words(search) } // returns a Flow instead of StateFlow
      .stateInViewModel(initialValue = emptyFlow())

  // util fun to launch operations in viewModel coroutine scope
  private fun effect(block: suspend () -> Unit) {
    viewModelScope.launch(Dispatchers.IO) { block() }
  }

  fun load() = effect {
    _isLoading.value = true
    allWords.value = wordRepository.allWords()
    _isLoading.value = false
  }

  // use allWords or searchWords depending on if the search is null or empty
  private fun words(search: String?) = when {
    search.isNullOrEmpty() -> allWords
    else -> searchWords
  }

  // return the Flow as a StateFlow
  private fun <T> Flow<T>.stateInViewModel(initialValue: T): StateFlow<T> =
    stateIn(
      // Stateflow is bound to viewModelScope
      scope = viewModelScope,
      // waits for a collector before emitting any values
      started = SharingStarted.Lazily,
      initialValue = initialValue
    )

  fun search(term: String?) = effect {
    _search.value = term
    // if search term isn't null, update searchWords with new term
    if (term != null) {
      searchWords.value = wordRepository.allWords(term)
    }
  }
}






































