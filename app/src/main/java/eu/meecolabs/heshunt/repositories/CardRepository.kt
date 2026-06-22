package eu.meecolabs.heshunt.repositories

import eu.meecolabs.heshunt.data.cards.LocalCards
import eu.meecolabs.heshunt.db.AppDatabase
import eu.meecolabs.heshunt.db.CollectedCardEntity
import eu.meecolabs.heshunt.model.Availability
import eu.meecolabs.heshunt.model.Card
import eu.meecolabs.heshunt.model.CardCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single
import java.time.LocalDate

interface CardRepository {
    fun getCards(): Flow<List<Card>>

    suspend fun toggleCardCollected(cardId: String, isCollected: Boolean)
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
                Card(
                    id = dto.id,
                    name = dto.name,
                    category = if (dto.category.lowercase() == "rare") CardCategory.RARE else CardCategory.MAIN,
                    description = dto.description,
                    siteIds = dto.siteIds,
                    isCollected = collected.any { it.cardId == dto.id },
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
}
