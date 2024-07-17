package com.nikita.webrequestmonitortesttask

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.nikita.webrequestmonitortesttask.data.remote.GoogleSearchAccessibilityService
import com.nikita.webrequestmonitortesttask.ui.RequestListScreen
import com.nikita.webrequestmonitortesttask.ui.theme.WebRequestMonitorTheme
import com.nikita.webrequestmonitortesttask.viewmodel.RequestViewModel
import org.koin.android.ext.android.inject
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    private val viewModel: RequestViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebRequestMonitorTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RequestListScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val isAccessibilityServiceEnabled = checkAccessibilityServiceEnabled(this)
        viewModel.setAccessibilityServiceEnabled(isAccessibilityServiceEnabled)
        checkAndRefreshRequests()
    }

    private fun checkAndRefreshRequests() {
        runBlocking {
            val hasRequests = viewModel.getAllRequestsOnce().isNotEmpty()
            if (hasRequests && !viewModel.isAccessibilityServiceEnabled.value) {
                viewModel.showServiceButHasRequestsScreen()
            } else {
                viewModel.refreshRequests()
            }
        }
    }

    private fun checkAccessibilityServiceEnabled(context: Context): Boolean {
        val expectedComponentName = ComponentName(context, GoogleSearchAccessibilityService::class.java)
        val enabledServicesSetting = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: return false
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServicesSetting)
        while (colonSplitter.hasNext()) {
            val componentNameString = colonSplitter.next()
            val enabledComponentName = ComponentName.unflattenFromString(componentNameString)
            if (enabledComponentName != null && enabledComponentName == expectedComponentName) {
                return true
            }
        }
        return false
    }
}