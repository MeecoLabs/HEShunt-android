/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.usecase

import eu.meecolabs.heshunt.repositories.CardRepository
import org.koin.core.annotation.Factory

@Factory
class ToggleCardCollectedUseCase(
    private val repository: CardRepository
) {
    suspend operator fun invoke(cardId: String, isCollected: Boolean) =
        repository.toggleCardCollected(cardId, isCollected)
}
