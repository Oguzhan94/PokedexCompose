package com.example.pokedexcompose.domain.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.pokedexcompose.data.model.PokedexListEntry
import com.example.pokedexcompose.data.model.pokemon.Pokemon
import com.example.pokedexcompose.data.network.PokeApi
import com.example.pokedexcompose.domain.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PokemonRepository @Inject constructor(
    private val pokemonApi: PokeApi
) {

    fun getPokemonList(): Flow<PagingData<PokedexListEntry>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = { PokemonPagingSource(pokemonApi) }
        ).flow
    }

    suspend fun getPokemonInfo(pokemonName: String): Flow<Resource<Pokemon>> {
        return flow {
            emit(Resource.Loading())
            val response = pokemonApi.getPokemonInfo(pokemonName)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(it.message))
        }
    }
}
