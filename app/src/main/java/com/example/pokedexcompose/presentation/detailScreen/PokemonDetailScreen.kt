package com.example.pokedexcompose.presentation.detailScreen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.toBitmap
import com.example.pokedexcompose.R
import com.example.pokedexcompose.data.model.PokedexListEntry
import com.example.pokedexcompose.data.model.pokemon.Pokemon
import com.example.pokedexcompose.utils.getPokemonColor
import com.example.pokedexcompose.utils.getPokemonStatColor

@Composable
fun PokemonDetailScreen(
    pokemon: PokedexListEntry,
    viewModel: PokemonDetailViewModel = hiltViewModel(),
    animatedVisibilityScope: AnimatedVisibilityScope,
    navController: NavController,
) {
    val uiState by viewModel.uiState
    var dominantColor by remember { mutableStateOf(Color.Gray) }

    LaunchedEffect(pokemon) {
        viewModel.getPokemonInfo(pokemon.pokemonName)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
    ) {
        Header(pokemon, viewModel, dominantColor, navController) { color ->
            dominantColor = color
        }
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            text = pokemon.pokemonName
        )
        when (uiState) {
            is PokemonDetailViewModel.HomeUiState.Error -> {
                ErrorMessage((uiState as PokemonDetailViewModel.HomeUiState.Error).message!!)
            }

            PokemonDetailViewModel.HomeUiState.Loading -> Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            is PokemonDetailViewModel.HomeUiState.Success -> {
                (uiState as PokemonDetailViewModel.HomeUiState.Success).data?.let { PokemonDetail(it) }
            }
        }
    }
}


@Composable
fun Header(
    pokemon: PokedexListEntry,
    viewModel: PokemonDetailViewModel,
    dominantColor: Color,
    navController: NavController,
    onDominantColorChange: (Color) -> Unit,
) {
    val shape = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart = 64.dp,
        bottomEnd = 64.dp,
    )
    val pokemonId = "#${pokemon.number.toString().padStart(3, '0')}"
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 9.dp, shape)
            .background(dominantColor, shape),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(dominantColor)
                .padding(start = 10.dp, top = 20.dp, bottom = 15.dp, end = 10.dp),
        ) {
            Icon(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable { navController.navigateUp() },
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                tint = Color.White,
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(horizontal = 5.dp),
                text = pokemon.pokemonName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier.padding(horizontal = 5.dp),
                text = pokemonId,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        AsyncImage(
            modifier = Modifier
                .padding(20.dp)
                .size(200.dp),
            model = ImageRequest.Builder(LocalContext.current)
                .data(pokemon.imageUrl)
                .crossfade(true)
                .allowHardware(false)
                .listener(
                    onSuccess = { _, result ->
                        val bitmap = result.image.toBitmap()
                        viewModel.extractDominantColor(bitmap) { color ->
                            onDominantColorChange(color)
                        }
                    }
                )
                .build(),
            contentDescription = "${pokemon.pokemonName} image",
            contentScale = ContentScale.Fit,
            error = painterResource(R.drawable.ic_launcher_foreground)

        )
    }
}

@Composable
fun ErrorMessage(errorMessage: String) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = errorMessage,
            color = Color.Red,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PokeType(pokeDetail: Pokemon) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(22.dp, Alignment.CenterHorizontally),
    ) {
        pokeDetail.types.forEach {
            Text(
                modifier = Modifier
                    .background(
                        color = getPokemonColor(it.type.name),
                        shape = RoundedCornerShape(40.dp),
                    )
                    .padding(horizontal = 30.dp, vertical = 5.dp),
                text = it.type.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }
}

@Composable
fun PokePhysicalType(pokeDetail: Pokemon) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .padding(5.dp),
                text = "Height",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Text(
                text = String.format("%.1f M", pokeDetail.height.toFloat() / 10),
                color = Color.White
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                modifier = Modifier
                    .padding(5.dp),
                text = "Weight",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Text(
                text = String.format("%.1f KG", pokeDetail.weight.toFloat() / 10),
                color = Color.White
            )
        }
    }
}

@Composable
fun PokeStats(pokeDetail: Pokemon) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 25.dp)
            .padding(horizontal = 15.dp)
    ) {
        pokeDetail.stats.forEach {
            val animatedColor by rememberInfiniteTransition().animateColor(
                initialValue = getPokemonStatColor(it.stat.name),
                targetValue = getPokemonStatColor(it.stat.name).copy(green = 1f, blue = 0.5f),
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ), label = ""
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp)
            ) {
                Text(
                    text = it.stat.name.uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = getPokemonStatColor(it.stat.name)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = it.base_stat.toString(),
                    color = animatedColor,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun PokemonDetail(pokeDetail: Pokemon) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
    ) {
        PokeType(pokeDetail)
        Spacer(modifier = Modifier.height(40.dp))
        PokePhysicalType(pokeDetail)
        Spacer(modifier = Modifier.height(65.dp))
        PokeStats(pokeDetail)
    }
}