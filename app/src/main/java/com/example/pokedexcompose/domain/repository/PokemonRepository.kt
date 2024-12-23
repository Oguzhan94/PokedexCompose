package com.example.pokedexcompose.domain.repository

import com.example.pokedexcompose.data.model.PokemonList
import com.example.pokedexcompose.data.model.pokemon.Pokemon
import com.example.pokedexcompose.data.network.PokeApi
import com.example.pokedexcompose.domain.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PokemonRepository @Inject constructor(
    private val pokemonApi: PokeApi
) {

    fun getPokemonList(limit: Int, offset: Int) : Flow<Resource<PokemonList>> = flow {
        try {
            emit(Resource.Loading())
            val response = pokemonApi.getPokemonList(limit, offset)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getPokemonInfo(pokemonName: String) : Flow<Resource<Pokemon>> {
        return flow{
            emit(Resource.Loading())
            val response = pokemonApi.getPokemonInfo(pokemonName)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(it.message))
        }
    }
}