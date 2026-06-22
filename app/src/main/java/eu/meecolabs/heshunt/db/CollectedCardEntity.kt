package eu.meecolabs.heshunt.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collected_cards")
data class CollectedCardEntity(
    @PrimaryKey
    val cardId: String,
    val collectedAt: Long
)
