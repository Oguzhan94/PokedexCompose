@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalSharedTransitionApi::class
)

package com.example.pokedexcompose.presentation.listScreen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.toBitmap
import com.example.pokedexcompose.R
import com.example.pokedexcompose.data.model.PokedexListEntry

@Composable
fun SharedTransitionScope.PokemonListScreen(
    viewModel: PokemonListScreenViewModel = hiltViewModel(),
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val uiState by viewModel.uiState
    val searchQuery by viewModel.searchQuery.collectAsState()
    val pokemonPagingItems = viewModel.pokemonList.collectAsLazyPagingItems()

    //animasyon calismiyor, farkli bir cozum ara
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        Header()
        Spacer(modifier = Modifier.height(10.dp))
        SearchSection(searchQuery = searchQuery) { query ->
            viewModel.updateSearchQuery(query)
        }
        Spacer(modifier = Modifier.height(50.dp))

        when {
            searchQuery.isEmpty() -> PagingListSection(
                pagingItems = pokemonPagingItems,
                navController = navController,
                viewModel = viewModel,
                animatedVisibilityScope = animatedVisibilityScope
            )

            else -> SearchResultSection(
                uiState = uiState,
                navController = navController,
                viewModel = viewModel,
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    }
}

@Composable
fun SearchSection(searchQuery: String, onQueryChange: (String) -> Unit) {
    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        inputField = {
            SearchBarDefaults.InputField(
                query = searchQuery,
                onQueryChange = onQueryChange,
                onSearch = {},
                expanded = false,
                onExpandedChange = {},
                placeholder = { Text("Input Pokemon name") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            )
        },
        expanded = false,
        onExpandedChange = {},
    ) {}
}

@Composable
fun SharedTransitionScope.PagingListSection(
    pagingItems: LazyPagingItems<PokedexListEntry>,
    navController: NavController,
    viewModel: PokemonListScreenViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    pagingItems.apply {
        when (loadState.refresh) {
            is LoadState.Loading -> LoadingIndicator()
            is LoadState.Error -> ErrorMessage((loadState.refresh as LoadState.Error).error.localizedMessage)
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(pagingItems.itemCount) { index ->
                    val pokemon = pagingItems[index]
                    pokemon?.let {
                        CardItem(
                            pokemon = it,
                            navController = navController,
                            viewModel = viewModel,
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SharedTransitionScope.SearchResultSection(
    uiState: PokemonListScreenViewModel.HomeUiState,
    navController: NavController,
    viewModel: PokemonListScreenViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    when (uiState) {
        is PokemonListScreenViewModel.HomeUiState.Loading -> LoadingIndicator()

        is PokemonListScreenViewModel.HomeUiState.Success -> {
            val pokemonList = uiState.data
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(pokemonList) { pokemon ->
                    CardItem(
                        pokemon = pokemon,
                        navController,
                        viewModel = viewModel,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            }
        }

        is PokemonListScreenViewModel.HomeUiState.Error -> ErrorMessage(uiState.message)
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(message: String?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message ?: "An error occurred.",
            color = Color.Red,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Header() {
    Column(
        modifier = Modifier.padding(start = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "POKEDEX",
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Search for a Pokémon",
            color = Color.White,
        )
    }
}

@Composable
fun SharedTransitionScope.CardItem(
    pokemon: PokedexListEntry,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PokemonListScreenViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    var dominantColor by remember { mutableStateOf(Color.Gray) }
    Card(
        modifier = modifier
            .padding(6.dp)
            .fillMaxWidth()
            .clickable {
                navController.currentBackStackEntry?.savedStateHandle?.set("pokemon", pokemon)
                navController.navigate("detailScreen")
            }
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardColors(
            containerColor = dominantColor,
            contentColor = dominantColor,
            disabledContentColor = dominantColor,
            disabledContainerColor = dominantColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
                .sharedElement(
                    rememberSharedContentState(key = "image-${pokemon.number}"),
                    animatedVisibilityScope = animatedVisibilityScope
                ),
            model = ImageRequest.Builder(LocalContext.current)
                .data(pokemon.imageUrl)
                .crossfade(true)
                .allowHardware(false)
                .listener(
                    onSuccess = { _, result ->
                        val bitmap = result.image.toBitmap()
                        viewModel.extractDominantColor(bitmap) { color ->
                            dominantColor = color
                        }
                    }
                )
                .build(),
            contentDescription = "${pokemon.pokemonName} Image",
            contentScale = ContentScale.Fit,
            error = painterResource(R.drawable.ic_launcher_foreground)
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .sharedElement(
                    state = rememberSharedContentState(key = "text-${pokemon.number}"),
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
            text = pokemon.pokemonName,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}