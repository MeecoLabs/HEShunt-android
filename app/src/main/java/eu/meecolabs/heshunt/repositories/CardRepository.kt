/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.repositories

import eu.meecolabs.heshunt.data.db.AppDatabase
import eu.meecolabs.heshunt.data.db.entities.CollectedCardEntity
import eu.meecolabs.heshunt.data.localcards.LocalCards
import eu.meecolabs.heshunt.model.Availability
import eu.meecolabs.heshunt.model.Card
import eu.meecolabs.heshunt.model.CardCategory
import eu.meecolabs.heshunt.model.Property
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

interface CardRepository {
    fun getCards(): Flow<List<Card>>

    suspend fun toggleCardCollected(cardId: String, isCollected: Boolean)

    suspend fun updateCollected(cardId: String, date: Long, property: Property?)
}

@Single(binds = [CardRepository::class])
class CardRepositoryImpl(
    private val localCards: LocalCards,
    private val database: AppDatabase
) : CardRepository {
    override fun getCards(): Flow<List<Card>> {
        val cardDtos = localCards.loadCards()

        return database.collectedCardDao().getAllCollectedCards().combine(flow { emit(cardDtos) }) { collected, dtos ->
            dtos.map { dto ->
                val collectionInfo = collected.firstOrNull { it.cardId == dto.id }
                Card(
                    id = dto.id,
                    name = dto.name,
                    category = if (dto.category.lowercase() == "rare") CardCategory.RARE else CardCategory.MAIN,
                    description = dto.description,
                    siteIds = dto.siteIds,
                    collectedOn = collectionInfo?.collectedAt?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    },
                    collectedAt = collectionInfo?.collectedFrom,
                    availability = dto.availability?.map {
                        Availability(
                            from = LocalDate.parse(it.from),
                            until = LocalDate.parse(it.until),
                            siteIds = it.siteIds
                        )
                    } ?: emptyList()
                )
            }
        }
    }

    override suspend fun toggleCardCollected(cardId: String, isCollected: Boolean) {
        if (isCollected) {
            database.collectedCardDao().insert(CollectedCardEntity(cardId, System.currentTimeMillis()))
        } else {
            database.collectedCardDao().delete(cardId)
        }
    }

    override suspend fun updateCollected(cardId: String, date: Long, property: Property?) {
        database.collectedCardDao().update(CollectedCardEntity(cardId, date, property?.id))
    }
}
