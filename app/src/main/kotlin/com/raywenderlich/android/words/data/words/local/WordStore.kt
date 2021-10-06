package com.raywenderlich.android.words.data.words.local

import androidx.paging.*
import com.raywenderlich.android.words.data.words.AppDatabase
import com.raywenderlich.android.words.data.words.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WordStore(database: AppDatabase) {
  //internal instance of WordDao
  private val words = database.words

  // calls all using WordDao to access LocalWord instances
  // then, map converts them to plain Words.
//  fun all(): List<Word> = words.queryAll().map { it.fromLocal() }
  fun all(): Flow<PagingData<Word>> = pagingWord { words.queryAll() }
  fun all(term: String): Flow<PagingData<Word>> = pagingWord { words.searchAll(term) }

  // Takes a list of plain Words using save, converts them to Room values and saves them
  suspend fun  save(words: List<Word>) {
    this.words.insert(words.map { it.toLocal() })
  }

  // determine if there are any saved words
  suspend fun isEmpty(): Boolean = words.count() == 0L
}

private fun Word.toLocal() = LocalWord(value =  value)
private fun LocalWord.fromLocal() = Word(value =  value)

// using Pager to convert a PagingSource to a Flow of PagingData
private fun pagingWord(
  block: () -> PagingSource<Int, LocalWord>
): Flow<PagingData<Word>> =
  Pager(PagingConfig(pageSize = 20)) { block() }.flow
    // converts each PagingData's LocalWords to regular Word instances
    .map { page ->
      page.map { localWord -> localWord.fromLocal() }
    }