package com.example.pokedexcompose.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PokedexListEntry(
    val pokemonName: String,
    val imageUrl: String,
    val number: Int
) : Parcelable