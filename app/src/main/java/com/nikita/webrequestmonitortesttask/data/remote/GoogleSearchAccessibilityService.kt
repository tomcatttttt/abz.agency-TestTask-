package com.nikita.webrequestmonitortesttask.data.remote

import android.accessibilityservice.AccessibilityService
import android.content.ContentValues
import android.net.Uri
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.nikita.webrequestmonitortesttask.data.local.RequestContentProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class GoogleSearchAccessibilityService : AccessibilityService() {

    private var lastRequestTime: Long = 0

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        Timber.d("Event received: %d", event.eventType)

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {

            val rootNode = rootInActiveWindow ?: return
            val urlNode = findUrlNode(rootNode)
            if (urlNode != null && urlNode.text != null) {
                val url = urlNode.text.toString()
                Timber.d("Detected URL: %s", url)
                if (url.contains("google.com/search")) {
                    handleSearchQuery(url)
                }
            }
        }
    }

    override fun onInterrupt() {
        Timber.d("Service interrupted")
    }

    private fun findUrlNode(rootNode: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        val nodeInfoQueue: MutableList<AccessibilityNodeInfo> = mutableListOf(rootNode)
        while (nodeInfoQueue.isNotEmpty()) {
            val node = nodeInfoQueue.removeAt(0)
            if (node.className == "android.widget.EditText" || node.className == "android.widget.TextView") {
                if (node.viewIdResourceName != null && node.viewIdResourceName.contains("url_bar")) {
                    return node
                }
            }
            for (i in 0 until node.childCount) {
                val childNode = node.getChild(i)
                if (childNode != null) {
                    nodeInfoQueue.add(childNode)
                }
            }
        }
        return null
    }

    private fun handleSearchQuery(url: String) {
        val uri = Uri.parse(url)
        if (!uri.isHierarchical) {
            Timber.d("Non-hierarchical URI, skipping: %s", url)
            return
        }
        val searchQuery = uri.getQueryParameter("q") ?: ""
        val currentTime = System.currentTimeMillis()

        if (searchQuery.isNotEmpty()) {
            CoroutineScope(Dispatchers.Default).launch {
                val values = ContentValues().apply {
                    put("requestText", searchQuery)
                    put("dateTime", currentTime)
                    put("websiteLink", url)
                }
                val insertedUri = contentResolver.insert(RequestContentProvider.CONTENT_URI, values)
                Timber.d("Inserted URI: %s", insertedUri)
                lastRequestTime = currentTime
            }
        }
    }
}