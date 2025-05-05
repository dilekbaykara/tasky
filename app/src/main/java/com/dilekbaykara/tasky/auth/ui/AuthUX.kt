package com.dilekbaykara.tasky.auth.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dilekbaykara.tasky.auth.dialogs.ErrorDialog
import com.dilekbaykara.tasky.auth.dialogs.SuccessDialog

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
fun RegistrationSheet(modifier: Modifier,
                      viewModel: AuthViewModel,
                      onLoginClick: () -> Unit,
                      onRegistrationSuccess: () -> Unit
)
{

    val context = LocalContext.current

    val shoulShowSuccessDialog = remember { mutableStateOf(false) }
    val regState by viewModel.registerState.collectAsState()
    var email by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var password by remember {  mutableStateOf("") }

    LaunchedEffect(regState) {
        if(regState?.isSuccess == true) {
            shoulShowSuccessDialog.value = true
        } else {
            Toast.makeText(context, "YOU FAILED", Toast.LENGTH_LONG).show()
        }
    }

    if(fullName.length < 4) {
        Toast.makeText(LocalContext.current, "Please enter more than 4 characters for name", Toast.LENGTH_LONG).show()
    }

    if(shoulShowSuccessDialog.value) {
        SuccessDialog(title = "Registration Successful", "Successfully created account for: $email", onRegistrationSuccess)
    }

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
                    AuthButton { viewModel.registration(fullName, email, password) }
                    AlreadyHaveAnAccountMessage(Modifier.padding(10.dp)) { onLoginClick() }
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

    val loginState by viewModel.loginState.collectAsState()
    val shoulShowSuccessDialog = remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember {  mutableStateOf("") }

    LaunchedEffect(loginState) {
        if(loginState?.isSuccess == true) {
            shoulShowSuccessDialog.value = true
        } else {
            Toast.makeText(context, "YOU FAILED", Toast.LENGTH_LONG).show()
        }
    }

    if(shoulShowSuccessDialog.value) {
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

    Column  {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White,
            shape = RoundedCornerShape(24.dp)
        ) {
            Box ( modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp, start = 16.dp, end = 16.dp)
                ) {
                    EmailField(email) {email = it}
                    PasswordField(password) {password = it}
                    AuthButton { viewModel.login(email, password) }
                    DoNotHaveAnAccountMessage { onRegisterClick() }
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