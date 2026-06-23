package eu.meecolabs.heshunt.ui.screens.detail

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.meecolabs.heshunt.BuildConfig
import eu.meecolabs.heshunt.model.Card
import eu.meecolabs.heshunt.model.CardStatus
import eu.meecolabs.heshunt.model.Property
import eu.meecolabs.heshunt.ui.components.PropertyPopup
import eu.meecolabs.heshunt.ui.screens.detail.components.StatusBadge
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.asBoolean
import org.maplibre.compose.expressions.dsl.condition
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.dsl.switch
import org.maplibre.compose.expressions.value.SymbolAnchor
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.material3.ExpandingAttributionButton
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.style.rememberStyleState
import org.maplibre.compose.util.ClickResult
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.Feature.Companion.getStringProperty
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    cardId: String,
    onBackClick: () -> Unit,
    viewModel: CardDetailViewModel = koinViewModel { parametersOf(cardId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(eu.meecolabs.heshunt.R.drawable.ic_back),
                            contentDescription = stringResource(eu.meecolabs.heshunt.R.string.back_button)
                        )
                    }
                },
                title = {
                    Text(text = stringResource(eu.meecolabs.heshunt.R.string.card_details_title))
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is UiState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Success -> {
                CardDetailContent(
                    card = state.card,
                    availableAt = state.availableAt,
                    allSites = state.allSites,
                    onToggleCollected = viewModel::toggleCollected,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }

            is UiState.Error -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    Text(text = state.message)
                }
            }
        }
    }
}

@Composable
private fun CardDetailContent(
    card: Card,
    availableAt: List<Property>,
    allSites: List<Property>,
    onToggleCollected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var showMap by remember { mutableStateOf(true) }
    var selectedProperty by remember { mutableStateOf<Property?>(null) }
    val status = remember(card) { card.getStatus(LocalDate.now()) }

    Column(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = card.name,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.weight(1f)
                    )

                    StatusBadge(status)
                }

                Text(
                    text = card.category.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = card.description,
                    style = MaterialTheme.typography.bodyLarge
                )

                if (card.availability.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(eu.meecolabs.heshunt.R.string.card_availability_periods),
                        style = MaterialTheme.typography.titleSmall
                    )

                    card.availability.forEach { period ->
                        val formatter = DateTimeFormatter.ofPattern("d MMM yyyy")
                        Text(
                            text = "• %s - %s".format(period.from.format(formatter), period.until.format(formatter)),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onToggleCollected(!card.isCollected) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (card.isCollected) stringResource(eu.meecolabs.heshunt.R.string.card_mark_missing) else stringResource(eu.meecolabs.heshunt.R.string.card_mark_collected)
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(eu.meecolabs.heshunt.R.string.card_available_at),
                style = MaterialTheme.typography.titleLarge
            )

            TextButton(onClick = { showMap = !showMap }) {
                Text(
                    text = if (showMap) stringResource(eu.meecolabs.heshunt.R.string.card_show_list) else stringResource(eu.meecolabs.heshunt.R.string.card_show_map)
                )
            }
        }

        if (showMap && allSites.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                CardDetailMap(
                    allSites = allSites,
                    availableAt = availableAt,
                    selectedProperty = selectedProperty,
                    onPropertyClick = { selectedProperty = it },
                    modifier = Modifier.fillMaxSize()
                )

                selectedProperty?.let { property ->
                    PropertyPopup(
                        property = property,
                        cards = null,
                        onDismiss = { selectedProperty = null },
                        onWebsiteClick = {
                            val intent = Intent(Intent.ACTION_VIEW, property.website.toUri())
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(allSites) { property ->
                    val isCurrentlyAvailable = availableAt.any { it.id == property.id }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = if (isCurrentlyAvailable) {
                            CardDefaults.cardColors()
                        } else {
                            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = property.name,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = property.localAuthority,
                                    style = MaterialTheme.typography.bodySmall
                                )

                                if (!isCurrentlyAvailable && (card.getStatus(LocalDate.now()) == CardStatus.ACTIVE)) {
                                    Text(
                                        text = stringResource(eu.meecolabs.heshunt.R.string.property_not_available),
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }

                            OutlinedButton(onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, property.website.toUri())
                                context.startActivity(intent)
                            }) {
                                Text(text = stringResource(eu.meecolabs.heshunt.R.string.property_website))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardDetailMap(
    allSites: List<Property>,
    availableAt: List<Property>,
    selectedProperty: Property?,
    onPropertyClick: (Property) -> Unit,
    modifier: Modifier = Modifier
) {
    val cameraState = rememberCameraState(
        firstPosition = remember(allSites.firstOrNull()?.id) {
            val first = allSites.firstOrNull()
            if (first != null) {
                CameraPosition(
                    target = Position(longitude = first.longitude, latitude = first.latitude),
                    zoom = 6.0,
                )
            } else CameraPosition()
        }
    )
    val styleState = rememberStyleState()

    val features = remember(allSites, availableAt, selectedProperty?.id) {
        allSites.map { property ->
            Feature(
                id = JsonPrimitive(property.id),
                geometry = Point(Position(longitude = property.longitude, latitude = property.latitude)),
                properties = buildJsonObject {
                    put("id", property.id)
                    put("active", availableAt.any { it.id == property.id })
                    put("selected", property.id == selectedProperty?.id)
                }
            )
        }
    }

    val geoJsonData = remember(features) {
        GeoJsonData.Features(FeatureCollection(features))
    }

    Box(modifier = modifier) {
        MaplibreMap(
            baseStyle = BaseStyle.Uri(BuildConfig.MAP_BASESTYLE_URI),
            cameraState = cameraState,
            styleState = styleState,
            options = MapOptions(ornamentOptions = OrnamentOptions.OnlyLogo),
            modifier = Modifier.matchParentSize()
        ) {
            val source = rememberGeoJsonSource(
                data = geoJsonData
            )

            val markerDefault = image(
                painterResource(org.maplibre.android.R.drawable.maplibre_marker_icon_default),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
            val markerSelected = image(
                painterResource(org.maplibre.android.R.drawable.maplibre_marker_icon_default),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
            )

            SymbolLayer(
                id = "property-layer",
                source = source,
                iconImage = switch(
                    condition(feature["selected"].asBoolean(), markerSelected),
                    fallback = markerDefault
                ),
                iconAnchor = const(SymbolAnchor.Bottom),
                iconAllowOverlap = const(true),
                iconIgnorePlacement = const(true),
                sortKey = switch(
                    condition(feature["selected"].asBoolean(), const(1f)),
                    fallback = const(0f),
                ),
                onClick = { clickedFeatures ->
                    val id = clickedFeatures.firstOrNull()?.getStringProperty("id")
                    allSites.find { it.id == id }?.let { onPropertyClick(it) }
                    ClickResult.Consume
                }
            )
        }

        Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            ExpandingAttributionButton(
                cameraState = cameraState,
                styleState = styleState,
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}
