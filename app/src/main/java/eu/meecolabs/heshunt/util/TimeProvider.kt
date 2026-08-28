/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.util

import java.time.LocalDate

interface TimeProvider {
    fun now(): LocalDate
}
