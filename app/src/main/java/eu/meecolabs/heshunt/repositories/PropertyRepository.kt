package eu.meecolabs.heshunt.repositories

import eu.meecolabs.heshunt.data.hes.HesAPI
import eu.meecolabs.heshunt.model.Property
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

interface PropertyRepository {
    suspend fun getProperties(): List<Property>
}

@Single(binds = [PropertyRepository::class])
class PropertyRepositoryImpl(
    private val hesAPI: HesAPI
) : PropertyRepository {
    private var cachedProperties: List<Property>? = null

    override suspend fun getProperties(): List<Property> = withContext(Dispatchers.IO) {
        cachedProperties?.let {
            return@withContext it
        }

        val geoJson = hesAPI.loadProperties()
        val properties = geoJson.features.map { feature ->
            Property(
                id = feature.properties.PIC_ID,
                name = feature.properties.PIC_NAME,
                longitude = feature.geometry.coordinates[0],
                latitude = feature.geometry.coordinates[1],
                website = feature.properties.LINK,
                localAuthority = feature.properties.LOCAL_AUTH
            )
        }
        cachedProperties = properties

        properties
    }
}
