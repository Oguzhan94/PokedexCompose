package com.example.pokedexcompose.presentation.listScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedexcompose.domain.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListScreenViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.getPokemonList(20, 1)
                .collect { response ->
                    response.data.let {
                        //datalar geliyor. ui olustur ve ui'a uygun model olustur.image lazim
                        //Result(name=ivysaur, url=https://pokeapi.co/api/v2/pokemon/2/)
                    }
                }
        }
    }

}