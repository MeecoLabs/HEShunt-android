/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.data.hes.models

import kotlinx.serialization.Serializable

@Serializable
data class PropertyMetadataDto(
    val PIC_ID: String,
    val PIC_NAME: String,
    val LINK: String,
    val LOCAL_AUTH: String
)
