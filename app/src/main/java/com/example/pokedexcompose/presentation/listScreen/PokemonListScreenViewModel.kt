package com.example.pokedexcompose.presentation.listScreen

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.palette.graphics.Palette
import coil3.Bitmap
import com.example.pokedexcompose.data.model.PokedexListEntry
import com.example.pokedexcompose.domain.Resource
import com.example.pokedexcompose.domain.repository.PokemonRepository
import com.example.pokedexcompose.domain.toPokeDexListEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PokemonListScreenViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<HomeUiState>(HomeUiState.Loading)
    val uiState: State<HomeUiState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _pokemonList = MutableStateFlow<PagingData<PokedexListEntry>>(PagingData.empty())
    val pokemonList: StateFlow<PagingData<PokedexListEntry>> = _pokemonList.asStateFlow()


    init {
        fetchPokemonList()
        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .filter { it.isNotBlank() }
                .distinctUntilChanged()
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
    }

    private fun searchPokemon(pokemonName: String) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            repository.getPokemonInfo(pokemonName).collect { responseState ->
                when (responseState) {
                    is Resource.Error -> {
                        _uiState.value =
                            HomeUiState.Error(
                                if (responseState.message.equals("HTTP 404 ")) "Pokemon bulunamadi"
                                else responseState.message
                            )
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

    fun extractDominantColor(bitmap: Bitmap, onColorReady: (Color) -> Unit) {
        viewModelScope.launch {
            val palette = Palette.from(bitmap).generate()
            val dominantColor = palette.getDominantColor(Color.Gray.toArgb())
            withContext(Dispatchers.Main) {
                onColorReady(Color(dominantColor))
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