package com.dilekbaykara.tasky.presentation.navigation.ux

sealed class TaskyScreen(val route: String) {
    object Login : TaskyScreen("login")
    object Registration : TaskyScreen("registration")
    object Main : TaskyScreen("main")
    object EventDetail : TaskyScreen("event_detail")
    object TaskDetail : TaskyScreen("task_detail")
    object ReminderDetail : TaskyScreen("reminder_detail")
    object EditTaskTitle : TaskyScreen("edit_task_title")
    object EditTaskDescription : TaskyScreen("edit_task_description")
    object PhotoDetail : TaskyScreen("photo_detail")
}
