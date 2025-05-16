package com.dilekbaykara.tasky.presentation.auth.register

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.input.TextObfuscationMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dilekbaykara.tasky.presentation.auth.error.SuccessDialog

@Composable
fun Header(modifier: Modifier = Modifier, text : String) {
    Text(
        text = AnnotatedString(text = text),
        color = Color.White,
        fontSize = 30.sp,
        fontStyle = (FontStyle.Normal),
        fontWeight = (FontWeight.Bold),
        modifier = modifier
    )
}

@Composable
fun RegistrationSheet(modifier: Modifier,
                      viewModel: AuthViewModel,
                      onLoginClick: () -> Unit,
                      onRegistrationSuccess: () -> Unit
)
{

    val context = LocalContext.current

    val shouldShowSuccessDialog = remember { mutableStateOf(false) }
    val regState by viewModel.registerState.collectAsState()
    var email by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var password by remember {  mutableStateOf("") }

    LaunchedEffect(regState) {
        if(regState?.isSuccess == true) {
            shouldShowSuccessDialog.value = true
        } else {
            Toast.makeText(context, "YOU FAILED", Toast.LENGTH_LONG).show()
        }
    }

    if(fullName.length < 4) {
        Toast.makeText(LocalContext.current, "Please enter more than 4 characters for name", Toast.LENGTH_LONG).show()
    }

    if(shouldShowSuccessDialog.value) {
        SuccessDialog(title = "Registration Successful", "Successfully created account for: $email", onRegistrationSuccess)
    }

    Surface(modifier = Modifier.fillMaxSize()
        .padding(top = 30.dp),
        color = Color.Black) {

        Column()  {
        Box(modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)){
        Header(modifier = Modifier.align(Alignment.Center).padding(30.dp),"Create your Account")
        }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White,
                shape = RoundedCornerShape(24.dp),
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 25.dp, start = 16.dp, end = 16.dp)
                    ) {
                        UsernameField(fullName) { fullName = it }
                        EmailField(email) { email = it }
                        PasswordField(password) { password = it }
                        AuthButton { viewModel.registration(fullName, email, password) }
                        AlreadyHaveAnAccountMessage(Modifier.padding(10.dp)) { onLoginClick() }
                    }
                }
            }
        }
    }
}



@Composable
fun LoginSheet(modifier: Modifier,
               viewModel: AuthViewModel,
               onLoginSuccess: () -> Unit,
               onRegisterClick: () -> Unit,
) {

    val context = LocalContext.current
    val errorDialog by viewModel.error.collectAsState()
    val loginState by viewModel.loginState.collectAsState()
    val shouldShowSuccessDialog = remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember {  mutableStateOf("") }


    LaunchedEffect(loginState) {
        if(loginState?.isSuccess == true) {
            shouldShowSuccessDialog.value = true
        } else {
            Toast.makeText(context, "YOU FAILED", Toast.LENGTH_LONG).show()
        }
    }

    errorDialog.let {
        Log.d("Error Message", "")
    }

    if(shouldShowSuccessDialog.value) {
        SuccessDialog(title = "Login Successful", "Welcome back, $email", onLoginSuccess)
    }


    // ERROR STATES: WIP
    //    val error by viewModel.error.collectAsState()
    //    var isErrorShowing = remember { mutableStateOf(false) }
    //    LaunchedEffect(error) {
    //        isErrorShowing.value = when (error) {
    //            is Error.Validation -> { true }
    //            is Error.ApiError -> { true }
    //            else -> false
    //        }
    //    }
    //
    //    if(isErrorShowing.value) {
    //        ErrorDialog(error) {
    //            viewModel.hideErrorDialog()
    //        }
    //    }

    Surface(modifier = Modifier.fillMaxSize()
        .padding(top = 30.dp),
        color = Color.Black) {

        Column() {
            Box(modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)) {
                Header(modifier = Modifier.align(Alignment.Center).padding(30.dp), "Welcome Back!")
            }
            Column {
                Surface(
                    modifier = Modifier.fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    onLoginSuccess()
                                }
                            )
                        },
                    color = Color.White,
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 35.dp, start = 16.dp, end = 16.dp)
                        ) {
                            EmailField(email) { email = it }
                            PasswordField(password) { password = it }
                            AuthButton { viewModel.login(email, password) }
                            DoNotHaveAnAccountMessage { onRegisterClick() }
                        }
                    }
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
    var showPassword by remember { mutableStateOf(false) }
    TextField(
        value = value,
        onValueChange = onChange,
        label = { Text("Password") },
        placeholder = {},
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(bottom = 16.dp, start = 15.dp, end = 15.dp)
            .fillMaxWidth(),
        visualTransformation = if (showPassword) {

            VisualTransformation.None

        } else {

            PasswordVisualTransformation()

        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            if (showPassword) {
                IconButton(onClick = { showPassword = false }) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = "hide_password"
                    )
                }
            } else {
                IconButton(
                    onClick = { showPassword = true }) {
                    Icon(
                        imageVector = Icons.Filled.VisibilityOff,
                        contentDescription = "hide_password"
                    )
                }
            }
        }
    )
}

@Composable
fun AuthButton(onClick: () -> Unit){
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
fun AlreadyHaveAnAccountMessage(
    modifier: Modifier,
    onLoginClick: () -> Unit
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
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {onLoginClick()}
        )
    }
}


@Composable
fun DoNotHaveAnAccountMessage(
    onRegistrationClick: () -> Unit
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "DON'T HAVE AN ACCOUNT? ",
            color = Color.Gray,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "CREATE ONE",
            color = Color.Blue,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {onRegistrationClick()}
        )
    }
}