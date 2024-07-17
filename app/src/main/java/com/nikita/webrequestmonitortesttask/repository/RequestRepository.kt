package com.nikita.webrequestmonitortesttask.repository

import com.nikita.webrequestmonitortesttask.data.local.UserRequest
import com.nikita.webrequestmonitortesttask.data.local.RequestDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import timber.log.Timber

class RequestRepository(private val requestDao: RequestDao) {

    val allRequestsFlow: Flow<List<UserRequest>> = requestDao.getAllRequests()

    suspend fun deleteRequest(id: Long) {
        Timber.d("Deleting request with ID: $id")
        requestDao.deleteRequestById(id)
    }

    suspend fun getAllRequestsOnce(): List<UserRequest> {
        return requestDao.getAllRequests().first()
    }

    suspend fun findRequestByText(requestText: String): UserRequest? {
        return requestDao.findRequestByText(requestText)
    }

}