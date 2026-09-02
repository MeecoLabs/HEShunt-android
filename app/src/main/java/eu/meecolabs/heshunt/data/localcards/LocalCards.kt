/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.data.localcards

import android.content.Context
import eu.meecolabs.heshunt.data.localcards.models.CardDto
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class LocalCards(
    private val context: Context,
    private val json: Json
) {
    fun loadCards(): List<CardDto> =
        context.assets
            .open("cards.json")
            .bufferedReader()
            .use { it.readText() }
            .let { json.decodeFromString<List<CardDto>>(it) }
}
