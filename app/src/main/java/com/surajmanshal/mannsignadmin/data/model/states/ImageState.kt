package com.surajmanshal.mannsignadmin.data.model.states

import android.net.Uri

data class ImageState(
    var id: Int? = null,
    var url: String? = null,
    var fileUri: Uri? = null,
    var description: String? = null,
    var languageId: Int,
    var langName: String = ""
)