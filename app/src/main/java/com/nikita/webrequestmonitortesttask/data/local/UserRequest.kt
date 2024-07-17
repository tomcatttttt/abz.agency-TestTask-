package com.nikita.webrequestmonitortesttask.data.local

import android.content.ContentValues
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "requests")
data class UserRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val requestText: String,
    val dateTime: Long,
    val websiteLink: String?
)

fun ContentValues.toUserRequest(): UserRequest {
    return UserRequest(
        id = this.getAsInteger("id") ?: 0,
        requestText = this.getAsString("requestText"),
        dateTime = this.getAsLong("dateTime"),
        websiteLink = this.getAsString("websiteLink")
    )
}