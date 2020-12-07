package com.dimi.advnotes.framework.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(items: List<T>): List<Long>

    @Update
    suspend fun update(Item: T): Int

    @Update
    suspend fun update(items: List<T>)

    @Delete
    suspend fun delete(Item: T): Int

    @Delete
    suspend fun delete(items: List<T>)
}