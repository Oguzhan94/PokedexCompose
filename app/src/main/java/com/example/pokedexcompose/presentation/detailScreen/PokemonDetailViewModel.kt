package com.example.pokedexcompose.presentation.detailScreen

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedexcompose.data.model.pokemon.Pokemon
import com.example.pokedexcompose.domain.Resource
import com.example.pokedexcompose.domain.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<HomeUiState>(HomeUiState.Loading)
    val uiState: State<HomeUiState> = _uiState

    fun getPokemonInfo(pokemonId: String) {
        viewModelScope.launch {
            repository.getPokemonInfo(pokemonId.replaceFirstChar { it.lowercase() })
                .collect { response ->
                    when (response) {
                        is Resource.Error -> {
                            _uiState.value =
                                HomeUiState.Error(response.message ?: "Bilinmeyen hata")
                        }

                        is Resource.Loading -> _uiState.value = HomeUiState.Loading
                        is Resource.Success -> {
                            _uiState.value = HomeUiState.Success(response.data)
                        }
                    }
                }
        }
    }

    @Stable
    sealed interface HomeUiState {
        data object Loading : HomeUiState

        data class Error(val message: String?) : HomeUiState

        data class Success(val data: Pokemon?) : HomeUiState
    }
}