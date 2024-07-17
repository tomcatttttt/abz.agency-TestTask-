package com.nikita.webrequestmonitortesttask.data.local

import android.content.ContentProvider
import android.content.ContentValues
import android.content.ContentUris
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import androidx.room.Room
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class RequestContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.nikita.webrequestmonitortesttask.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/requests")

        const val REQUESTS = 1
        const val REQUEST_ID = 2

        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "requests", REQUESTS)
            addURI(AUTHORITY, "requests/#", REQUEST_ID)
        }
    }

    private lateinit var db: AppDatabase

    override fun onCreate(): Boolean {
        db = Room.databaseBuilder(
            context!!,
            AppDatabase::class.java, "request_database"
        ).build()
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return when (uriMatcher.match(uri)) {
            REQUESTS -> db.requestDao().getAllRequestsCursor()
            REQUEST_ID -> {
                val id = ContentUris.parseId(uri)
                db.requestDao().getRequestByIdCursor(id)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Timber.d("Inserting values: %s", values)
        if (values == null) {
            throw IllegalArgumentException("ContentValues must not be null")
        }
        val requestText = values.getAsString("requestText")
        val existingRequest = runBlocking {
            db.requestDao().findRequestByText(requestText)
        }
        if (existingRequest == null) {
            val id = runBlocking {
                val insertedId = db.requestDao().insert(values.toUserRequest())
                db.requestDao().deleteDuplicateRequests(requestText)
                insertedId
            }
            val resultUri = ContentUris.withAppendedId(CONTENT_URI, id)
            Timber.d("Inserted ID: %d with URI: %s", id, resultUri)
            return resultUri
        } else {
            Timber.d("Duplicate requestText: %s, not inserting.", requestText)
            return null
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return when (uriMatcher.match(uri)) {
            REQUEST_ID -> {
                val id = ContentUris.parseId(uri)
                val rowsDeleted = runBlocking {
                    db.requestDao().deleteRequestById(id)
                }
                rowsDeleted
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun getType(uri: Uri): String? {
        throw UnsupportedOperationException("Not yet implemented")
    }
}