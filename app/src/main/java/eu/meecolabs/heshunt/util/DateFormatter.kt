/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.util

import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)