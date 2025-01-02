package com.example.pokedexcompose.presentation.extensions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette
import coil3.Bitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun Bitmap.extractDominantColor(onColorReady: (Color) -> Unit) {
    CoroutineScope(Dispatchers.Default).launch {
        val palette = Palette.from(this@extractDominantColor).generate()
        val dominantColor = palette.getDominantColor(Color.Gray.toArgb())
        withContext(Dispatchers.Main) {
            onColorReady(Color(dominantColor))
        }
    }
}
