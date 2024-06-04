package com.example.epubtest2

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.epubtest2.ui.theme.EpubTest2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            EpubTest2Theme {
                Surface (
                    modifier = Modifier.fillMaxSize().safeContentPadding()
                ){
                    MyApp(context = this)
                }
            }
        }
    }
}

@Composable
fun MyApp(
    context: Context
) {
    val navController = rememberNavController()
    SetupNavGraph(
        startDestination = Screens.MainRoute.route,
        navController = navController,
        context = context
    )
}
