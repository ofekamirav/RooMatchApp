package com.example.roomatchapp.di

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.roomatchapp.BuildConfig
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.android.policy.GlobalUploadPolicy
import com.example.roomatchapp.data.base.StringCallback
import com.example.roomatchapp.utils.toFile
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

class CloudinaryModel {

    companion object {
        private var isInitialized = false

        fun init(context: Context) {
            if (!isInitialized) {
                val config = mapOf(
                    "cloud_name" to BuildConfig.CLOUD_NAME,
                    "api_key" to BuildConfig.API_KEY,
                    "api_secret" to BuildConfig.API_SECRET
                )
                MediaManager.init(context, config)
                MediaManager.get().globalUploadPolicy = GlobalUploadPolicy.defaultPolicy()
                isInitialized = true
            }
        }
    }

    suspend fun uploadImage(
        bitmap: Bitmap,
        name: String,
        folder: String,
        onSuccess: StringCallback,
        onError: StringCallback,
        context: Context
    ): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val context = context
            if (context == null) {
                Log.e("Cloudinary", "Context is null. Cannot upload image.")
                continuation.resume(false)
                return@suspendCancellableCoroutine
            }

            if (!isInitialized) {
                Log.e("Cloudinary", "MediaManager is not initialized!")
                onError("MediaManager is not initialized!")
                continuation.resume(false)
                return@suspendCancellableCoroutine
            }

            val file: File = bitmap.toFile(context, name)
            Log.d("Cloudinary", "Starting upload to Cloudinary. File path: ${file.path}")

            MediaManager.get().upload(file.path)
                .option("folder", folder)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {}

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String
                        if (url != null) {
                            Log.d("Cloudinary", "Upload successful. URL: $url")
                            onSuccess(url)
                            continuation.resume(true)
                        } else {
                            Log.e("Cloudinary", "Upload failed: No URL returned")
                            onError("Upload failed: No URL returned")
                            continuation.resume(false)
                        }
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        Log.e("Cloudinary", "Upload failed. Error: ${error?.description}")
                        onError(error?.description ?: "Unknown error")
                        continuation.resume(false)
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                })
                .dispatch()
        }
    }
}