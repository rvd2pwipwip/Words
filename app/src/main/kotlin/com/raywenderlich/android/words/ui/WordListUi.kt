package com.raywenderlich.android.words.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.raywenderlich.android.words.data.words.Word
import com.raywenderlich.android.words.ui.bars.MainTopBar
import com.raywenderlich.android.words.ui.bars.SearchBar
import kotlinx.coroutines.flow.Flow

@Composable
fun WordListUi(
  words: Flow<PagingData<Word>>,
  search: String?,
  onSearch: (String?) -> Unit
) {
  Scaffold(
    topBar = {
      SearchBar(
        search = search,
        onSearch = onSearch
      )
    },
    content = {
      WordsContent(
        words = words,
        onSelected = { word ->
          Log.e("WordsContent", "Selected: $word")
        }
      )
    }
  )
}

@Composable
private fun WordColumnItem(
  word: Word,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .clickable { onClick() },
  ) {
    Text(
      text = word.value,
      modifier = Modifier.padding(16.dp)
    )
  }
}

@Composable
fun WordsContent(
  words: Flow<PagingData<Word>>,
  onSelected: (Word) -> Unit
) {
  // collecting the pages into LazyPagingItems instance
  val items: LazyPagingItems<Word> = words.collectAsLazyPagingItems()
  LazyColumn {
    // overloading the items function with the Paging library
    items(items = items) { word ->
      // checking if the item is null or not
      if (word != null) {
        WordColumnItem(word = word) {
          onSelected(word)
        }
      }
    }
    if (items.itemCount == 0) {
      item { EmptyContent() }
    }
  }
}

@Composable
private fun LazyItemScope.EmptyContent() {
  Box(
    modifier = Modifier.fillParentMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    Text(text = "No words")
  }
}



































