package eu.meecolabs.heshunt.ui.screens.detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import eu.meecolabs.heshunt.BuildConfig
import eu.meecolabs.heshunt.model.Property
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
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

@Composable
internal fun CardDetailMap(
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
                iconAnchor = const(SymbolAnchor.Center),
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
