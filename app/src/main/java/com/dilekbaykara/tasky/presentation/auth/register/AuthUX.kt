package com.dilekbaykara.tasky.presentation.auth.register

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dilekbaykara.tasky.R
import com.dilekbaykara.tasky.domain.model.FabItem
import com.dilekbaykara.tasky.domain.model.OptionItem
import com.dilekbaykara.tasky.domain.model.OptionItemType
import com.dilekbaykara.tasky.presentation.auth.error.SuccessDialog


@Composable
fun Header(modifier: Modifier = Modifier, text : String) {
    Text(
        text = AnnotatedString(text = text),
        fontSize = 20.sp,
        fontStyle = (FontStyle.Normal),
        fontWeight = (FontWeight.Bold),
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge,
    )
}



@Preview
@Composable
fun CalendarButton(modifier: Modifier = Modifier) {
    var isToggled by rememberSaveable { mutableStateOf(false) }
    IconButton(
        onClick = {isToggled = !isToggled}
    ) {
        Icon(
            painter = painterResource(R.drawable.calendar_today),
            contentDescription = "",
            modifier.height(100.dp)
        )
    }
}

@Composable
fun AvatarDropDown(modifier: Modifier) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = {expanded = !expanded}) {
        Icon(
            painter = painterResource(R.drawable.avatar_bg),
            contentDescription = ""
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Log Out") },
                onClick = {}
            )
        }
    }
}

val fabItemList = listOf(
    FabItem(
        icon = R.drawable.calendar_today,
        title = "Event",
    ),
    FabItem(
        icon = R.drawable.done,
        title = "Task",
    ),
    FabItem(
        icon = R.drawable.notification_icon,
        title = "Reminder",
    )
)

@Composable
fun TaskyFab(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember {
        mutableStateOf(FabItem(R.drawable.calendar_today, "Event"))
    }
    FloatingActionButton(
        onClick = {  expanded = !expanded },
        modifier = Modifier
    ) {
        Icon(Icons.Filled.Add, "Floating action button.")
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },

        ) {
        fabItemList.forEachIndexed {
                _, _ ->

            DropdownMenuItem(
                text = {
                    Row {
                        Icon(painter = painterResource(R.drawable.calendar_today), contentDescription = "")
                        Text(OptionItemType.Delete.toString())

                    }
                },
                onClick = { /* Do something... */ }
            )

        }
    }
}





@Composable
fun MoreDropdownMenu() {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .padding(5.dp)
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(painter = painterResource(R.drawable.more_icon), contentDescription = "More Options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },

            ) {
            DropdownMenuItem(
                text = { Text(OptionItemType.Open.toString()) },
                onClick = { /* Do something... */ }
            )
            DropdownMenuItem(
                text = { Text(OptionItemType.Edit.toString()) },
                onClick = { /* Do something... */ }
            )
            DropdownMenuItem(
                text = { Text(OptionItemType.Delete.toString()) },
                onClick = { /* Do something... */ }
            )

        }

    }

}


@Composable
fun OptionItemsList(optionItems : List<OptionItem>) {
    Box(
        Modifier
            .background(MaterialTheme.colorScheme.inverseSurface)
            .height(120.dp)
            .border(width = 0.dp, brush = Brush.sweepGradient(), shape = RoundedCornerShape(50.dp))
    ) {
        LazyColumn {
            items(optionItems.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(15.dp)
                        .background(Color.Transparent)
                        .wrapContentSize()
                ) {
                    OptionListItem(
                        optionItems[index],
                        text = "",
                    )
                }
            }
        }

    }
}


@Composable
fun OptionListItem(optionItem: OptionItem, text: String){
    Button(onClick = {}, modifier = Modifier.background(MaterialTheme.colorScheme.inverseSurface)) {
        Text("")
    }
}




@Composable
fun DeleteButton() {
    // isToggled initial value should be read from a view model or persistent storage.
    var isToggled by rememberSaveable { mutableStateOf(false) }

    IconButton(
        onClick = { isToggled = !isToggled }
    ) {
        Icon(
            painter = painterResource(R.drawable.trash_icon),
            contentDescription = if (isToggled) "Selected icon button" else "Unselected icon button."
        )
    }
}


@Composable
fun TaskButton() {
    // isToggled initial value should be read from a view model or persistent storage.
    var isToggled by rememberSaveable { mutableStateOf(false) }

    IconButton(
        onClick = { isToggled = !isToggled }
    ) {
        Icon(
            painter = if (isToggled) painterResource(R.drawable.done) else painterResource(R.drawable.not_done_icon),
            contentDescription = if (isToggled) "Selected icon button" else "Unselected icon button."
        )
    }
}

@Composable
fun EditButton() {
    var isToggled by rememberSaveable { mutableStateOf(false) }

    IconButton(
        onClick = { isToggled = !isToggled }
    ) {
        Icon(
            painter = painterResource(R.drawable.edit_icon),
            contentDescription = ""
        )
    }
}

@Composable
fun CloseOutButton() {
    var isToggled by rememberSaveable { mutableStateOf(false) }

    IconButton(
        onClick = { isToggled = !isToggled }
    ) {
        Icon(
            painter = painterResource(R.drawable.x),
            contentDescription = ""
        )
    }
}


@Composable
fun AddAgendaItemFab(){
    var isToggled by rememberSaveable { mutableStateOf(false) }

    IconButton(
        onClick = { isToggled = !isToggled }
    ) {
        Icon(
            painter = painterResource(R.drawable.plus),
            contentDescription = ""
        )
    }
}

@Composable
fun NotificationIcon(){
    Icon(
        painter = painterResource(R.drawable.notification_icon),
        contentDescription = ""
    )
}


@Composable
fun CheckMarkIcon(){
    Icon(
        painter = painterResource(R.drawable.check),
        contentDescription = ""
    )
}



@Composable
fun AgendaDescription(modifier: Modifier = Modifier, text: String) {
    Text(
        text = AnnotatedString(text = text),
        fontSize = 20.sp,
        fontStyle = (FontStyle.Normal),
        fontWeight = (FontWeight.Normal),
        modifier = modifier,
        style = MaterialTheme.typography.titleSmall
    )
}

@Composable
fun AgendaDateTime(modifier: Modifier = Modifier, text: String) {
    Text(
        text = AnnotatedString(text = text),
        fontSize = 20.sp,
        fontStyle = (FontStyle.Normal),
        fontWeight = (FontWeight.Normal),
        modifier = modifier,
        style = MaterialTheme.typography.titleSmall
    )
}

@Composable
fun AttendeeButton(modifier: Modifier = Modifier, text: String) {
    Text(
        text = AnnotatedString(text = text),
        fontSize = 15.sp,
        fontStyle = (FontStyle.Normal),
        fontWeight = (FontWeight.Normal),
        modifier = modifier
            .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(10.dp))
            .padding(5.dp),
        style = MaterialTheme.typography.titleSmall
    )
}


@Composable
fun DatePickers(modifier: Modifier = Modifier, text : Pair<Char,Int>){
    Column(modifier = Modifier) {
        Text(
            text = AnnotatedString(text = text.first.toString()),
            fontSize = 20.sp,
            fontStyle = (FontStyle.Normal),
            fontWeight = (FontWeight.Bold),
            modifier = modifier,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = AnnotatedString(text = text.second.toString()),
            fontSize = 20.sp,
            fontStyle = (FontStyle.Normal),
            fontWeight = (FontWeight.Bold),
            modifier = modifier,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}


@Composable
fun RegistrationSheet(
    modifier: Modifier,
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

    Surface(modifier = Modifier
        .fillMaxSize()
        .padding(top = 30.dp),
        color = MaterialTheme.colorScheme.inverseSurface) {

        Column()  {
            Box(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)){
                Header(modifier = Modifier
                    .align(Alignment.Center)
                    .padding(30.dp),"Create your Account")
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



    Surface(modifier = Modifier
        .fillMaxSize()
        .padding(top = 30.dp),
        color = MaterialTheme.colorScheme.inverseSurface) {

        Column() {
            Box(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)) {
                Header(modifier = Modifier
                    .align(Alignment.Center)
                    .padding(30.dp),  "Welcome Back!")
            }
            Column {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsernameField(value: String, onChange:(String) -> Unit ){
    TextField(
        value = value,
        onValueChange = onChange,
        label = { Text("Name") },
        placeholder = {},
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
            .padding(bottom = 20.dp, top = 20.dp, start = 15.dp, end = 15.dp)
            .fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailField(value: String, onChange:(String) -> Unit){
    TextField(
        value = value,
        onValueChange = onChange,
        label = { Text("Email Address") },
        placeholder = {},
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
            .padding(bottom = 16.dp, start = 15.dp, end = 15.dp)
            .fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
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
