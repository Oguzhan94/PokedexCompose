package com.example.pokedexcompose.presentation.listScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.pokedexcompose.data.model.PokedexListEntry
import com.example.pokedexcompose.domain.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListScreenViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _pokemonList = MutableStateFlow<PagingData<PokedexListEntry>>(PagingData.empty())
    val pokemonList: StateFlow<PagingData<PokedexListEntry>> = _pokemonList.asStateFlow()

    init {
        fetchPokemonList()
    }

    private fun fetchPokemonList() {
        viewModelScope.launch {
            repository.getPokemonList()
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _pokemonList.value = pagingData
                }
        }
    }

}