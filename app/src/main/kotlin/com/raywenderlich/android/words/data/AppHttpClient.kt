package com.raywenderlich.android.words.data

import io.ktor.client.*

val AppHttpClient: HttpClient by lazy {
  HttpClient()
}
