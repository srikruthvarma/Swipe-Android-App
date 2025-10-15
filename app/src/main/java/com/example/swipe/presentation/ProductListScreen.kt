package com.example.swipe.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.swipe.R
import com.example.swipe.data.model.Product
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen() {
    val viewModel: ProductViewModel = koinViewModel()
    val uiState by viewModel.allProductsState.collectAsState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    var showRefreshedText by remember { mutableStateOf(false) }

    val onRefreshClicked = {
        viewModel.fetchProducts()
        scope.launch {
            showRefreshedText = true
            delay(2000L)
            showRefreshedText = false
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Products")
                        Spacer(Modifier.weight(1f))
                        AnimatedVisibility(
                            visible = showRefreshedText && scrollBehavior.state.collapsedFraction < 0.5,
                            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                        ) {
                            Text("Refreshed!", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(end = 8.dp))
                        }
                        IconButton(
                            onClick = { onRefreshClicked() },
                            modifier = Modifier.graphicsLayer { alpha = 1f - scrollBehavior.state.collapsedFraction },
                            enabled = scrollBehavior.state.collapsedFraction < 0.5
                        ) {
                            Icon(Icons.Default.Refresh, "Refresh", Modifier.size(32.dp))
                        }
                    }
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AnimatedVisibility(
                            visible = showRefreshedText && scrollBehavior.state.collapsedFraction > 0.5,
                            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                        ) {
                            Text("Refreshed!", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(end = 4.dp))
                        }
                        IconButton(
                            onClick = { onRefreshClicked() },
                            modifier = Modifier.graphicsLayer { alpha = scrollBehavior.state.collapsedFraction },
                            enabled = scrollBehavior.state.collapsedFraction > 0.5
                        ) {
                            Icon(Icons.Default.Refresh, "Refresh")
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text("Search products") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(50)
            )

            when {
                uiState.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                uiState.error != null -> OfflineScreen()
                else -> {
                    if (uiState.filteredProducts.isEmpty() && uiState.searchQuery.isNotEmpty()) {
                        Box(Modifier.fillMaxSize().padding(16.dp), Alignment.Center) {
                            Text("No products found for '${uiState.searchQuery}'.")
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(16.dp)) {
                            items(uiState.filteredProducts) { ProductListItem(it) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OfflineScreen() {
    Box(Modifier.fillMaxSize().padding(16.dp), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.CloudOff, "Offline", Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Spacer(Modifier.height(16.dp))
            Text("Oops! Looks like you're offline.", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            Text("Please check your connection and try again.", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun ProductListItem(product: Product) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        ListItem(
            headlineContent = { Text(product.productName, fontWeight = FontWeight.Medium) },
            supportingContent = { Text(product.productType) },
            leadingContent = {
                AsyncImage(
                    model = product.image.takeIf { !it.isNullOrBlank() },
                    placeholder = painterResource(R.drawable.placeholder_image),
                    error = painterResource(R.drawable.placeholder_image),
                    contentDescription = product.productName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp))
                )
            },
            trailingContent = {
                Column(horizontalAlignment = Alignment.End) {
                    Text("₹${"%.2f".format(product.price)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Text("Tax: ${product.tax}%", style = MaterialTheme.typography.bodySmall)
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}