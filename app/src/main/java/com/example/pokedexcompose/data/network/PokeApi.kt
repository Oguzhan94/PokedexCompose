package com.example.pokedexcompose.data.network

import com.example.pokedexcompose.data.model.PokemonList
import com.example.pokedexcompose.data.model.pokemon.Pokemon
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApi{

    @GET
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int
    ): PokemonList

    @GET("pokemon/{name}")
    suspend fun getPokemonInfo(
        @Path("name") name: String
    ): Pokemon

}