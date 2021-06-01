package com.example.memer.MODELS

import android.net.Uri

data class GalleryItem(
    val uri: Uri,
    val name: String,
    val size: Int
)

data class TemplatesItem(
    val id:String
)
