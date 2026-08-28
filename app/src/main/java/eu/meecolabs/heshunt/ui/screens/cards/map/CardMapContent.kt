/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.ui.screens.cards.map

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import eu.meecolabs.heshunt.BuildConfig
import eu.meecolabs.heshunt.model.Property
import eu.meecolabs.heshunt.ui.components.PropertyPopup
import eu.meecolabs.heshunt.ui.screens.cards.UiState
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.asBoolean
import org.maplibre.compose.expressions.dsl.condition
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.switch
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.material3.ExpandingAttributionButton
import org.maplibre.compose.overlay.MapOverlay
import org.maplibre.compose.overlay.MaplibreLogo
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

@Composable
internal fun CardMapContent(
    state: UiState.Success,
    selectedProperty: Property?,
    onSelectProperty: (Property?) -> Unit,
    onCardClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val cameraState = rememberCameraState(
        firstPosition = remember(state.properties.firstOrNull()?.id) {
            val first = state.properties.firstOrNull()
            if (first != null) {
                CameraPosition(
                    target = Position(longitude = first.longitude, latitude = first.latitude),
                    zoom = 6.0,
                )
            } else CameraPosition()
        }
    )
    val styleState = rememberStyleState()

    val features = remember(state.properties, selectedProperty?.id) {
        state.properties.map { property ->
            Feature(
                id = JsonPrimitive(property.id),
                geometry = Point(Position(longitude = property.longitude, latitude = property.latitude)),
                properties = buildJsonObject {
                    put("id", property.id)
                    put("selected", property.id == selectedProperty?.id)
                },
            )
        }
    }

    val geoJsonData = remember(features) {
        GeoJsonData.Features(FeatureCollection(features))
    }

    Box(modifier = modifier.fillMaxSize()) {
        MaplibreMap(
            baseStyle = BaseStyle.Uri(BuildConfig.MAP_BASESTYLE_URI),
            cameraState = cameraState,
            styleState = styleState,
            overlay = MapOverlay {
                MaplibreLogo(Modifier.align(Alignment.BottomStart))

                ExpandingAttributionButton(
                    cameraState = cameraState,
                    styleState = styleState,
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            },
            modifier = Modifier.fillMaxSize()
        ) {
            // Always needs to be inside MaplibreMap composable or app will crash!
            val source = rememberGeoJsonSource(
                data = geoJsonData
            )

            val circleDefault = const(MaterialTheme.colorScheme.primary)
            val circleSelected = const(MaterialTheme.colorScheme.secondary)

            CircleLayer(
                id = "property-layer",
                source = source,
                color = switch(
                    condition(feature["selected"].asBoolean(), circleSelected),
                    fallback = circleDefault
                ),
                sortKey = switch(
                    condition(feature["selected"].asBoolean(), const(1f)),
                    fallback = const(0f)
                ),
                onClick = { clickedFeatures ->
                    val feature = clickedFeatures.firstOrNull()
                    val id = feature?.getStringProperty("id")
                        ?: feature?.id?.content
                    val selectedProperty = state.properties.find { it.id == id }
                    onSelectProperty(selectedProperty)
                    ClickResult.Consume
                }
            )
        }

        selectedProperty?.let { property ->
            val cards = remember(property, state.allCards) {
                state.allCards.filter { it.card.isAssociatedWith(property.id) }
            }
            PropertyPopup(
                property = property,
                cards = cards,
                onDismiss = { onSelectProperty(null) },
                onWebsiteClick = {
                    val intent = Intent(Intent.ACTION_VIEW, property.website.toUri())
                    context.startActivity(intent)
                },
                onCardClick = {
                    onCardClick(it.card.id)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp)
            )
        }
    }
}
