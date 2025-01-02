package com.example.pokedexcompose.presentation.listScreen

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.pokedexcompose.data.model.PokedexListEntry
import com.example.pokedexcompose.domain.Resource
import com.example.pokedexcompose.domain.repository.PokemonRepository
import com.example.pokedexcompose.domain.toPokeDexListEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonListScreenViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _pokemonList = MutableStateFlow<PagingData<PokedexListEntry>>(PagingData.empty())
    val pokemonList: StateFlow<PagingData<PokedexListEntry>> = _pokemonList.asStateFlow()


    init {
        fetchPokemonList()
        observeSearchQuery()
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(2000)
                .filter { it.isNotBlank() }
                .collect { pokemonName ->
                    searchPokemon(pokemonName.lowercase())
                }
        }
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

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _uiState.value = HomeUiState.Loading
    }

    private fun searchPokemon(pokemonName: String) {
        viewModelScope.launch {
            repository.getPokemonInfo(pokemonName).collect { responseState ->
                when (responseState) {
                    is Resource.Error -> {
                        _uiState.value = HomeUiState.Error(responseState.message)
                    }

                    is Resource.Loading -> _uiState.value = HomeUiState.Loading

                    is Resource.Success -> {
                        if (responseState.data?.name != null) {
                            val pokedexEntries = responseState.data.toPokeDexListEntry().let {
                                listOf(it)
                            }
                            _uiState.value = HomeUiState.Success(pokedexEntries)
                        } else {
                            _uiState.value = HomeUiState.Loading
                        }
                    }
                }
            }
        }
    }

    @Stable
    sealed interface HomeUiState {
        data object Loading : HomeUiState

        data class Error(val message: String?) : HomeUiState

        data class Success(val data: List<PokedexListEntry>) : HomeUiState
    }
}