package com.example.pokedexcompose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun getPokemonColor(type: String): Color {
    return when (type) {
        "fighting" -> Color(0xFFC22E28)
        "flying" -> Color(0xFFA98FF3)
        "poison" -> Color(0xFFA040A0)
        "ground" -> Color(0xFFE2BF65)
        "rock" -> Color(0xFFB6A136)
        "bug" -> Color(0xFFA8B820)
        "ghost" -> Color(0xFF735797)
        "steel" -> Color(0xFFB7B7CE)
        "fire" -> Color(0xFFEE8130)
        "water" -> Color(0xFF6390F0)
        "grass" -> Color(0xFF7AC74C)
        "electric" -> Color(0xFFF7D02C)
        "psychic" -> Color(0xFFF95587)
        "ice" -> Color(0xFF96D9D6)
        "dragon" -> Color(0xFF6F35FC)
        "fairy" -> Color(0xFFD685AD)
        "dark" -> Color(0xFF705746)
        else -> Color(0xFFA8A77A)
    }
}

@Composable
fun getPokemonStatColor(stat: String): Color {
    return when (stat) {
        "hp" -> Color(0xFFFF5959)
        "attack" -> Color(0xFFF5AC78)
        "defense" -> Color(0xFFFAE078)
        "speed" -> Color(0xFF85C7F2)
        "special-attack" -> Color(0xFF9D5BFF)
        "special-defense" -> Color(0xFF4FC1A6)

        else -> Color.White
    }
}