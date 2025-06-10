package com.dilekbaykara.tasky.features.agenda.presentation.events
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class AttendeeManager @Inject constructor() : ViewModel() {
    data class AttendeeData(
        val email: String,
        val fullName: String,
        val userId: String,
        val isCreator: Boolean = false,
        val isGoing: Boolean = true
    )
    enum class AttendeeFilter {
        ALL, GOING, NOT_GOING
    }
    data class AttendeeValidationResult(
        val isValid: Boolean,
        val attendeeData: AttendeeData? = null,
        val errorMessage: String? = null
    )
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    suspend fun validateAndFetchAttendee(email: String): AttendeeValidationResult {
        return if (isValidEmail(email)) {
            AttendeeValidationResult(
                isValid = true,
                attendeeData = AttendeeData(
                    email = email,
                    fullName = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                    userId = "user_${email.hashCode()}",
                    isCreator = false,
                    isGoing = true
                )
            )
        } else {
            AttendeeValidationResult(
                isValid = false,
                errorMessage = "Invalid email format"
            )
        }
    }
    fun getCurrentUserId(): String {
        return "current_user_id"
    }
    fun filterAttendees(
        attendees: List<AttendeeData>,
        filter: AttendeeFilter
    ): List<AttendeeData> {
        return when (filter) {
            AttendeeFilter.ALL -> attendees
            AttendeeFilter.GOING -> attendees.filter { it.isGoing }
            AttendeeFilter.NOT_GOING -> attendees.filter { !it.isGoing }
        }
    }
}