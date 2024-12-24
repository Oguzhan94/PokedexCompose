package com.example.pokedexcompose.domain.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.pokedexcompose.data.model.PokedexListEntry
import com.example.pokedexcompose.data.network.PokeApi

//paging library. daha once bilmiyordum!!, sayfalama yapmak icin kullan.
class PokemonPagingSource(
    private val pokemonApi: PokeApi
) : PagingSource<Int, PokedexListEntry>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokedexListEntry> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize

            val response = pokemonApi.getPokemonList(
                offset = page * pageSize,
            )
            val pokeDexEntries = response.results.map { it.toPokedexListEntry() }
            Log.d("PokemonPagingSource", "Response: $response")
            LoadResult.Page(
                data = pokeDexEntries,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (pokeDexEntries.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PokedexListEntry>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
