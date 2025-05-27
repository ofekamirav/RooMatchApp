package com.example.roomatchapp.presentation.screens.owner.properties

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.roomatchapp.R
import com.example.roomatchapp.data.base.StringCallback
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.owner.property.PropertiesViewModel
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.RooMatchAppTheme
import com.example.roomatchapp.presentation.theme.Secondary
import com.example.roomatchapp.presentation.theme.cardBackground
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


@Composable
fun PropertiesScreen(
    onAddProperty: () -> Unit,
    onPropertyClick: StringCallback,
    viewModel: PropertiesViewModel
) {
    val properties by viewModel.properties.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { viewModel.refreshContent() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(2.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            LoadingAnimation(
                isLoading = isLoading,
                animationResId = R.raw.loading_animation
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Properties",
                        style = MaterialTheme.typography.titleLarge,
                        color = Primary,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (properties.isEmpty()) {
                        Text(
                            text = "No properties available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Secondary,
                        )
                        Text(
                            "Add a property and publish it so new renters can find it!",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    else{
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 16.dp)
                        ) {
                            items(properties) { property ->
                                PropertyRow(
                                    property = property,
                                    onPropertyClick = { propertyId->
                                        onPropertyClick(propertyId)
                                    },
                                    viewModel = viewModel
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }

                }
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    FloatingActionButton(
                        onClick = { onAddProperty() },
                        containerColor = Color.Unspecified,
                        contentColor = Color.Unspecified,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.size(60.dp),
                        elevation = FloatingActionButtonDefaults.elevation(0.dp),
                        content = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_add),
                                contentDescription = "Add Property",
                                modifier = Modifier.size(60.dp),
                                tint = Color.Unspecified
                            )
                        }
                    )
                }
            }
        }
    }

}


@Composable
fun PropertyRow(
    property: Property,
    viewModel: PropertiesViewModel,
    onPropertyClick: StringCallback
) {
    val context = LocalContext.current
    val updatingPropertyId by viewModel.updatingPropertyId.collectAsState()
    val isChecked = property.available ?: false
    val isLoading = updatingPropertyId == property.id


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                property.id.let {
                    onPropertyClick(it)
                }
            },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.cardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = if (property.photos.isNotEmpty()) {
                        rememberAsyncImagePainter(property.photos[0])
                    } else {
                        painterResource(id = R.drawable.ic_location)
                    },
                    modifier = Modifier.size(64.dp).clip(CircleShape),
                    contentDescription = "Property Image",
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = property.title ?: "Property Title",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = property.address ?: "Property Address",
                        fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                        color = Color.Gray,
                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Available",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = Primary
                    )
                } else {
                    Switch(
                        checked = isChecked,
                        onCheckedChange = {
                            viewModel.toggleAvailability(context, property.id)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            uncheckedThumbColor = Color.Gray,
                            checkedTrackColor = Primary,
                            uncheckedTrackColor = Color.LightGray
                        ),
                        thumbContent = if (isChecked) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Home,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        } else {
                            null
                        }
                    )
                }
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun PropertiesScreenPreview(){
    RooMatchAppTheme {
//        PropertiesScreen(onAddProperty = {})
    }
}