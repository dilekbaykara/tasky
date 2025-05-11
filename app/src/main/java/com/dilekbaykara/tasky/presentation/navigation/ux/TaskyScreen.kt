package com.dilekbaykara.tasky.presentation.navigation.ux

sealed class TaskyScreen(val route: String) {
    object Login : TaskyScreen("login")
    object Registration : TaskyScreen("registration")
    object Main : TaskyScreen("main")
}
