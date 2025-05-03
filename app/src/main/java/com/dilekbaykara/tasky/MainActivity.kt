package com.dilekbaykara.tasky


import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dilekbaykara.tasky.auth.ui.AuthViewModel
import com.dilekbaykara.tasky.ui.theme.TaskyTheme
import dagger.hilt.android.AndroidEntryPoint






@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    // Bringing in viewModel
    private val viewModel: SplashViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()




    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        splashScreen.setKeepOnScreenCondition { viewModel.isLoading.value }
        setContent {
            TaskyTheme {
                Scaffold(topBar = {}, modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier
                        .background(Color.Black)
                        .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Header(
                            modifier = Modifier
                                .padding(innerPadding)
                                .padding(top = 40.dp)
                        )
                        RegistrationSheet(
                            modifier = Modifier
                                .fillMaxWidth(),
                            viewModel = authViewModel
                        )
                    }
                }
            }
        }
    }




    @Composable
    fun Header(modifier: Modifier = Modifier) {
        Text(
            text = "Create your account",
            color = Color.White,
            fontSize = 30.sp,
            fontStyle = (FontStyle.Normal),
            fontWeight = (FontWeight.Bold),
            modifier = modifier




        )
    }








    @Composable
    fun RegistrationSheet(modifier: Modifier, viewModel: AuthViewModel) {




        val regState by viewModel.registerState.collectAsState()


        if(regState?.isSuccess == true) {
            Toast.makeText(LocalContext.current, "YOU REGISTERED", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(LocalContext.current, "YOU FAILED", Toast.LENGTH_LONG).show()
        }


        var email by remember { mutableStateOf("") }
        var fullName by remember { mutableStateOf("") }
        var password by remember {  mutableStateOf("") }




        Column  {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White,
                shape = RoundedCornerShape(24.dp)
            ) {
                Box ( modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp, start = 16.dp, end = 16.dp)
                    ) {




                        UsernameField(fullName) {fullName = it}
                        EmailField(email) {email = it}
                        PasswordField(password) {password = it}




                        RegistrationButton {
                            viewModel.registration(fullName, email, password)
                        }
                        AccountMessage(Modifier.padding(10.dp))
                    }
                }
            }
        }
    }




    @Composable
    fun UsernameField(value: String, onChange:(String) -> Unit ){
        TextField(
            value = value,
            onValueChange = onChange,
            label = { Text("Name") },
            placeholder = {},
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .padding(bottom = 20.dp, top = 20.dp, start = 15.dp, end = 15.dp)
                .fillMaxWidth()
        )
    }




    @Composable
    fun EmailField(value: String, onChange:(String) -> Unit){
        TextField(
            value = value,
            onValueChange = onChange,
            label = { Text("Email Address") },
            placeholder = {},
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .padding(bottom = 16.dp, start = 15.dp, end = 15.dp)
                .fillMaxWidth()




        )
    }




    @Composable
    fun PasswordField(value: String, onChange:(String) -> Unit){
        TextField(
            value = value,
            onValueChange = onChange,
            label = { Text("Password") },
            placeholder = {},
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .padding(bottom = 16.dp, start = 15.dp, end = 15.dp)
                .fillMaxWidth()
        )
    }




    @Composable
    fun RegistrationButton(onClick: () -> Unit){
        Button(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, start = 15.dp, end = 15.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
            onClick = onClick
        ) {
            Text("GET STARTED", modifier = Modifier.padding(15.dp))
        }
    }




    @Composable
    fun AccountMessage(
        modifier: Modifier




    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp),




            horizontalArrangement = Arrangement.Center




        ) {
            Text(
                text = "ALREADY HAVE AN ACCOUNT? ",
                color = Color.Gray,
                fontWeight = FontWeight.Bold




            )
            Text(
                text = "LOG IN",
                color = Color.Blue,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
