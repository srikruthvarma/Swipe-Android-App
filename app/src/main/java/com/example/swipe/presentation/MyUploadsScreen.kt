package com.example.swipe.presentation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.swipe.R
import com.example.swipe.data.local.MyUpload
import org.koin.androidx.compose.koinViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyUploadsScreen() {
    val viewModel: ProductViewModel = koinViewModel()
    val myUploads by viewModel.myUploadsState.collectAsState()
    Scaffold(
        topBar = { TopAppBar(title = { Text("My Uploads") }) }
    ) { paddingValues ->
        if (myUploads.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(paddingValues), Alignment.Center) {
                Text("You haven't added any products yet.")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(paddingValues), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(16.dp)) {
                items(myUploads) { MyUploadListItem(it) }
            }
        }
    }
}
@Composable
fun MyUploadListItem(upload: MyUpload) {
    val alpha = if (!upload.isSynced) 0.5f else 1f
    ElevatedCard(Modifier.fillMaxWidth().graphicsLayer(alpha = alpha)) {
        ListItem(
            headlineContent = { Text(upload.productName, fontWeight = FontWeight.Medium) },
            supportingContent = { Text(upload.productType) },
            leadingContent = {
                AsyncImage(
                    model = upload.imageUri,
                    placeholder = painterResource(R.drawable.placeholder_image),
                    error = painterResource(R.drawable.placeholder_image),
                    contentDescription = upload.productName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp))
                )
            },
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!upload.isSynced) {
                        Icon(Icons.Default.Schedule, "Pending Sync", Modifier.size(16.dp).padding(end = 4.dp), tint = LocalContentColor.current.copy(alpha = 0.6f))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("₹${"%.2f".format(upload.price)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Text("Tax: ${upload.tax}%", style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}
