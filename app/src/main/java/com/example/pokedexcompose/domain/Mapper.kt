package com.example.pokedexcompose.domain

import com.example.pokedexcompose.data.model.PokedexListEntry
import com.example.pokedexcompose.data.model.Result
import com.example.pokedexcompose.data.model.pokemon.Pokemon
import java.util.Locale

fun Result.toPokeDexListEntry(): PokedexListEntry {
    val number = if (url.endsWith("/")) {
        url.dropLast(1).takeLastWhile { it.isDigit() }
    } else {
        url.takeLastWhile { it.isDigit() }
    }
    val imageUrl =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${number}.png"

    return PokedexListEntry(
        pokemonName = name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        },
        imageUrl = imageUrl,
        number = number.toInt()
    )
}

fun Pokemon.toPokeDexListEntry(): PokedexListEntry {
    val imageUrl =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
    return PokedexListEntry(
        pokemonName = name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        },
        imageUrl = imageUrl,
        number = id
    )
}
