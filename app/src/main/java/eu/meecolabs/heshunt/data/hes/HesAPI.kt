package eu.meecolabs.heshunt.data.hes

import eu.meecolabs.heshunt.data.hes.models.GeoJsonDto
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class HesAPI(
    @Named("hes")
    private val client: OkHttpClient
) {
    companion object {
        private const val URL = "https://inspire.hes.scot/arcgis/rest/services/HES/Properties_in_care_points/MapServer/0/query?where=1%3D1&outFields=*&f=geojson"
    }

    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun loadProperties(): GeoJsonDto {
        val request = Request.Builder()
            .url(URL)
            .build()
        val response = client.newCall(request).execute()

        val body = response.body.string()
        return json.decodeFromString<GeoJsonDto>(body)
    }
}
