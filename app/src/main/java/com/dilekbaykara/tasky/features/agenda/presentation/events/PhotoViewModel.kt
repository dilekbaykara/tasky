package com.dilekbaykara.tasky.features.agenda.presentation.events
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dilekbaykara.tasky.features.auth.data.AuthService
import com.dilekbaykara.tasky.features.shared.data.network.NetworkConnectivityManager
import com.dilekbaykara.tasky.features.shared.data.remote.TaskyApi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject
@HiltViewModel
class PhotoViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: TaskyApi,
    private val authService: AuthService,
    private val networkManager: NetworkConnectivityManager
) : ViewModel() {
    private val _photos = MutableStateFlow<List<String>>(emptyList())
    val photos: StateFlow<List<String>> = _photos.asStateFlow()
    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()
    private val _uploadProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
    val uploadProgress: StateFlow<Map<String, Float>> = _uploadProgress.asStateFlow()
    private val _isOnline = MutableStateFlow(networkManager.isOnline())
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    init {
        // Optionally, you could add a network callback to update _isOnline in real time
    }

    fun setPhotosFromEvent(photoUrls: List<String>) {
        Log.d("PhotoViewModel", "Setting photos from event: ${photoUrls.size} photos")
        _photos.value = photoUrls
    }
    fun clearPhotos() {
        Log.d("PhotoViewModel", "Clearing all photos")
        _photos.value = emptyList()
    }
    fun processAndUploadPhotos(uris: List<Uri>) {
        if (_photos.value.size + uris.size > 10) {
            showToastMessage("Maximum 10 photos allowed")
            return
        }

        viewModelScope.launch {
            uris.forEach { uri ->
                try {
                    val compressedImageData = compressImage(uri)
                    if (compressedImageData != null) {
                        val photoUrl = uploadPhotoToServer(compressedImageData, uri.toString())
                        if (photoUrl != null) {
                            _photos.value = _photos.value + photoUrl
                            Log.d("PhotoViewModel", "Successfully uploaded and added photo: $photoUrl")
                        } else {
                            // If upload fails, store the local URI temporarily
                            _photos.value = _photos.value + uri.toString()
                            Log.d("PhotoViewModel", "Upload failed, storing local URI: $uri")
                        }
                    }
                } catch (e: Exception) {
                    Log.d("PhotoViewModel", "Error processing photo: ${e.message}")
                }
            }
        }
    }
    private suspend fun uploadPhotoToServer(imageData: ByteArray, originalUri: String): String? {
        return try {
            if (!networkManager.isOnline()) {
                Log.d("PhotoViewModel", "Cannot upload: offline")
                return null
            }
            val requestBody = imageData.toRequestBody("image/jpeg".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("photo", "photo.jpg", requestBody)
            val response = api.uploadPhoto(multipartBody)
            if (response.isSuccessful) {
                val photoResponse = response.body()
                Log.d("PhotoViewModel", "Photo uploaded successfully: \\${photoResponse?.url}")
                photoResponse?.url
            } else {
                Log.d("PhotoViewModel", "Upload failed with code: \\${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.d("PhotoViewModel", "Upload exception: \\${e.message}")
            null
        }
    }
    private fun compressImage(uri: Uri): ByteArray? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val maxWidth = 1024
            val maxHeight = 1024
            val ratio = minOf(maxWidth.toFloat() / bitmap.width, maxHeight.toFloat() / bitmap.height)
            val newWidth = (bitmap.width * ratio).toInt()
            val newHeight = (bitmap.height * ratio).toInt()
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            val outputStream = ByteArrayOutputStream()
            var quality = 85
            do {
                outputStream.reset()
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                quality -= 5
            } while (outputStream.size() > 1024 * 1024 && quality > 10)
            Log.d("PhotoViewModel", "Compressed image to ${outputStream.size()} bytes")
            outputStream.toByteArray()
        } catch (e: Exception) {
            Log.d("PhotoViewModel", "Compression error: ${e.message}")
            null
        }
    }
    fun removePhoto(photoUrl: String) {
        Log.d("PhotoViewModel", "Removing photo: \\$photoUrl")
        _photos.value = _photos.value.filter { it != photoUrl }
        if (photoUrl.contains("http")) {
            viewModelScope.launch {
                try {
                    if (networkManager.isOnline()) {
                        val photoKey = photoUrl.substringAfterLast("/")
                        api.deletePhoto(photoKey)
                        Log.d("PhotoViewModel", "Photo deleted from server: \\$photoKey")
                    }
                } catch (e: Exception) {
                    Log.d("PhotoViewModel", "Error deleting photo from server: \\${e.message}")
                }
            }
        }
    }
    fun getPhotoUrls(): List<String> {
        return _photos.value
    }
    fun showToastMessage(message: String) {
        Log.d("PhotoViewModel", "Toast: $message")
    }
}
