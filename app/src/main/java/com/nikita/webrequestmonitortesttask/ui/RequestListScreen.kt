package com.nikita.webrequestmonitortesttask.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nikita.webrequestmonitortesttask.utils.ContextUtils
import com.nikita.webrequestmonitortesttask.viewmodel.RequestViewModel
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@Composable
fun RequestListScreen(viewModel: RequestViewModel = getViewModel(), contextUtils: ContextUtils = get()) {
    val requests by viewModel.allRequests.collectAsState()
    val isAccessibilityServiceEnabled by viewModel.isAccessibilityServiceEnabled.collectAsState()
    var sortByDescending by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.updateAccessibilityServiceStatus()
        viewModel.checkAndRefreshRequests()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (requests.isEmpty()) {
            when {
                isAccessibilityServiceEnabled -> EmptyScreen(contextUtils, "No requests found. Open your browser and search for something to start monitoring.", "Open Browser") {
                    contextUtils.openBrowser()
                }
                else -> EmptyScreen(contextUtils, "No requests found. Please enable the Accessibility Service to start monitoring.", "Enable Accessibility Service") {
                    contextUtils.openAccessibilitySettings()
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = "Request List",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (sortByDescending) "Sorted by: Date (Descending)" else "Sorted by: Date (Ascending)",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = { sortByDescending = !sortByDescending }) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                LazyColumn {
                    val sortedRequests = if (sortByDescending) {
                        requests.sortedByDescending { it.dateTime }
                    } else {
                        requests.sortedBy { it.dateTime }
                    }
                    items(sortedRequests) { request ->
                        RequestItem(userRequest = request, onDelete = { viewModel.deleteRequest(request.id) }, contextUtils = contextUtils)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { contextUtils.openBrowser() },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Open Browser", style = MaterialTheme.typography.labelLarge)
        }
    }
}
