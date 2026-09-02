/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.data.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import eu.meecolabs.heshunt.data.db.entities.CollectedCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectedCardDao {
    @Query("SELECT * FROM collected_cards")
    fun getAllCollectedCards(): Flow<List<CollectedCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(collectedCard: CollectedCardEntity)

    @Update
    suspend fun update(collectedCard: CollectedCardEntity)

    @Query("DELETE FROM collected_cards WHERE cardId = :cardId")
    suspend fun delete(cardId: String)
}
