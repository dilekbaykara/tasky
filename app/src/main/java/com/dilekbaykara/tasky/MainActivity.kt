package com.dilekbaykara.tasky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dilekbaykara.tasky.ui.theme.TaskyTheme

class MainActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        splashScreen.setKeepOnScreenCondition { viewModel.isLoading.value }
        setContent {
            TaskyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
//
//    @Composable
//    fun SimpleFilledTextFieldSample(
//        mainViewModel: MainViewModel = viewModel()
//    ) {
//        val mainUiState by mainViewModel.uiState.collectAsState()
//        Column(modifier = Modifier.padding(16.dp)) {
//            TextField(
//                value = mainUiState.currentName,
//                singleLine = true,
//                modifier = Modifier.fillMaxWidth(),
//                onValueChange = { mainViewModel.updateName(it) },
//                label = { Text(text = "Enter your Name") },
//                isError = mainUiState.currentNameErrors.isNotEmpty()
//            )
//            mainUiState.currentNameErrors.forEach {
//                Text(
//                    modifier = Modifier.padding(vertical = 8.dp),
//                    text = it,
//                    color = Color.RED
//                )
//            }
//        }
//    }


    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        TaskyTheme {
            Greeting("Android")
        }
    }
}