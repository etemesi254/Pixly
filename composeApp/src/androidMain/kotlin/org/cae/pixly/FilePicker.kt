package org.cae.pixly

import AppContext
import SUPPORTED_EXTENSIONS
import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalContext
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import loadImage
import java.io.File


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun showFilePicker(appContext: AppContext) {
    // Ask for permission
    val androidContext = LocalContext.current;

    val imageStorageState = rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)

    LaunchedEffect(appContext.showStates.showFilePicker) {
        if (appContext.showStates.showFilePicker && !imageStorageState.status.isGranted) {
            imageStorageState.launchPermissionRequest()
        }
    }
    // file picker code
    FilePicker(
        show = appContext.showStates.showFilePicker && imageStorageState.status.isGranted,
        fileExtensions = SUPPORTED_EXTENSIONS
    ) {
        appContext.showStates.showFilePicker = false
        if (it != null) {
            // check permission
            if (imageStorageState.status.isGranted) {
                Log.e("File", it.path);
                val path = getRealPathFromURI(androidContext, Uri.parse(it.path))
                if (path != null) {
                    appContext.imFile = File(path)
                    appContext.rootDirectory = appContext.imFile.parent;
                    loadImage(appContext,false)
                }
            }
        }
    }
}