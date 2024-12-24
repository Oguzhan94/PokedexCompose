package com.example.pokedexcompose.presentation.listScreen

import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import com.example.pokedexcompose.R
import com.example.pokedexcompose.data.model.PokedexListEntry

@Composable
fun PokemonListScreen(
    viewModel: ListScreenViewModel = hiltViewModel()
) {
    val pokemonPagingItems = viewModel.pokemonList.collectAsLazyPagingItems()

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(15.dp))


        PagingListSection(
            pagingItems = pokemonPagingItems,
            viewModel = viewModel
        )

    }

}

@Composable
fun PagingListSection(
    pagingItems: LazyPagingItems<PokedexListEntry>,
    viewModel: ListScreenViewModel
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
                            pokemon = it
                        )
                    }
                }
            }
        }
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
fun CardItem(
    pokemon: PokedexListEntry,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(6.dp)
            .fillMaxWidth()
            .clickable {}
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardColors(
            containerColor = Color.LightGray,
            contentColor = Color.LightGray,
            disabledContentColor = Color.LightGray,
            disabledContainerColor = Color.LightGray
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp),
            model = ImageRequest.Builder(LocalContext.current)
                .data(pokemon.imageUrl)
                .crossfade(true)
                .allowHardware(false)
                .listener(
                    onStart = {
                        // Handle the onStart event
                    },
                    onSuccess = { request, metadata ->
                        Log.d("Error", "Error: ${metadata.image}")
                    },
                    onError = { request, throwable ->
                        Log.d("Error", "Error: ${throwable.throwable}")
                    }
                )
                .build(),
            contentDescription = "${pokemon.pokemonName} Image",
            contentScale = ContentScale.Fit,
            error = painterResource(R.drawable.ic_launcher_foreground)
        )
        Text(
            text = pokemon.pokemonName,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            textAlign = TextAlign.Center
        )
    }
}
