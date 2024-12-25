package com.example.pokedexcompose.presentation.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NavigationComponent() {
    val navController = rememberNavController()
    SharedTransitionLayout {
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                NavHost(navController = navController, startDestination = "listScreen") {
                    composable("listScreen") {
                        PokemonListScreen(
                            navController = navController,
                            animatedVisibilityScope = this
                        )
                    }
                    composable("detailScreen") {
                        val pokemon = navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.get<PokedexListEntry>("pokemon")
                        pokemon?.let {
                            PokemonDetailScreen(
                                pokemon = it,
                                navController = navController,
                                animatedVisibilityScope = this
                            )
                        }
                    }
                }
            }
        }
    }
}