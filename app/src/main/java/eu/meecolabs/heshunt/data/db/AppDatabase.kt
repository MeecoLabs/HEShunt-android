/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import eu.meecolabs.heshunt.data.db.daos.CollectedCardDao
import eu.meecolabs.heshunt.data.db.entities.CollectedCardEntity

@Database(
    entities = [CollectedCardEntity::class],
    exportSchema = true,
    version = 2,
    autoMigrations = [
        AutoMigration(1, 2)
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun collectedCardDao(): CollectedCardDao
}
