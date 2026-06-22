package eu.meecolabs.heshunt.usecase

import eu.meecolabs.heshunt.model.Card
import eu.meecolabs.heshunt.repositories.CardRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetCardsUseCase(
    private val repository: CardRepository
) {
    operator fun invoke(): Flow<List<Card>> =
        repository.getCards()
}
