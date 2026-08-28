/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.util

import org.koin.core.annotation.Single
import java.time.LocalDate

@Single
class RealTimeProvider : TimeProvider {
    override fun now(): LocalDate =
        LocalDate.now()
}
