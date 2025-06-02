package com.dilekbaykara.tasky.presentation.events

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class PhotoViewModel : ViewModel() {
    var selectedBitmap = mutableStateOf<Bitmap?>(null)
    var selectedPhotoIndex = mutableStateOf<Int?>(null)
    var photos = mutableStateOf<List<Uri>>(emptyList())

    fun removePhotoAt(index: Int) {
        if (index in photos.value.indices) {
            photos.value = photos.value.toMutableList().also { it.removeAt(index) }
        }
    }
}