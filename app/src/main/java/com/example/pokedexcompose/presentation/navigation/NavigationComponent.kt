package com.example.pokedexcompose.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pokedexcompose.data.model.PokedexListEntry
import com.example.pokedexcompose.presentation.detailScreen.PokemonDetailScreen
import com.example.pokedexcompose.presentation.listScreen.PokemonListScreen

@Composable
fun NavigationComponent() {
    val navController = rememberNavController()
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(navController = navController, startDestination = "listScreen") {
                composable("listScreen") {
                    PokemonListScreen(navController = navController)
                }
                composable("detailScreen") {
                    val pokemon = navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<PokedexListEntry>("pokemon")
                    PokemonDetailScreen()
                }
            }
        }

    }
}