/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.data.cards

import android.content.Context
import eu.meecolabs.heshunt.data.cards.models.CardDto
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class LocalCards(
    private val context: Context
) {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun loadCards(): List<CardDto> {
        val cardsJson = context.assets.open("cards.json").bufferedReader().use { it.readText() }
        return json.decodeFromString<List<CardDto>>(cardsJson)
    }
}
