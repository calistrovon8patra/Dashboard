package com.github.dashboardapp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.github.dashboardapp.ui.home.HomeScreen
import com.github.dashboardapp.ui.theme.DashboardTheme
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DashboardTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HomeScreen()
                }
            }
        }
    }
}
