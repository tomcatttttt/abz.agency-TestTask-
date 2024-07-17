package com.nikita.webrequestmonitortesttask.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import android.database.Cursor
import kotlinx.coroutines.flow.Flow

@Dao
interface RequestDao {



    @Query("SELECT * FROM requests ORDER BY dateTime DESC")
    fun getAllRequests(): Flow<List<UserRequest>>


    @Query("SELECT MAX(dateTime) FROM requests")
    suspend fun getLatestRequestTime(): Long?



    @Insert
    suspend fun insert(request: UserRequest): Long

    @Query("DELETE FROM requests WHERE id = :id")
    suspend fun deleteRequestById(id: Long): Int

    @Query("SELECT * FROM requests WHERE id = :id")
    fun getRequestByIdCursor(id: Long): Cursor

    @Query("SELECT * FROM requests")
    fun getAllRequestsCursor(): Cursor

    @Query("SELECT * FROM requests WHERE requestText = :requestText")
    suspend fun findRequestByText(requestText: String): UserRequest?

    @Query("DELETE FROM requests WHERE requestText = :requestText AND id != (SELECT id FROM requests WHERE requestText = :requestText LIMIT 1)")
    suspend fun deleteDuplicateRequests(requestText: String)
}
