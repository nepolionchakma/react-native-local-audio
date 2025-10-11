package com.audiomodule

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import com.facebook.react.bridge.*

class AudioModule(private val reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String = "AudioModule"

    // Helper function to categorize based on file extension
    private fun categorizeAudio(filePath: String): String {
        return when {
            filePath.endsWith(".mp3", ignoreCase = true) -> "song"
            filePath.endsWith(".wav", ignoreCase = true) -> "song"
            filePath.endsWith(".m4a", ignoreCase = true) -> "song"
            filePath.endsWith(".ogg", ignoreCase = true) -> "song"
            filePath.endsWith(".flac", ignoreCase = true) -> "song"
            filePath.endsWith(".aac", ignoreCase = true) -> "song"
            filePath.endsWith(".3gp", ignoreCase = true) -> "recording"
            filePath.endsWith(".amr", ignoreCase = true) -> "recording"
            filePath.endsWith(".m4r", ignoreCase = true) -> "ringtone"
            filePath.endsWith(".mp4", ignoreCase = true) -> "video"
            else -> "unknown" // For any unknown file type
        }
    }

    // Query to fetch all audio data from MediaStore
    private fun querySongs(
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?,
        artworkQuality: Int?
    ): WritableNativeArray {
        val contentResolver: ContentResolver = reactContext.contentResolver
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
        val audioList = WritableNativeArray()

        cursor?.use {
            val idIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val pathIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val albumIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (it.moveToNext()) {
                val song = WritableNativeMap()
                val albumId = it.getLong(albumIdIndex)
                val filePath = it.getString(pathIndex) ?: ""

                song.putInt("id", it.getInt(idIndex))
                song.putString("title", it.getString(titleIndex))
                song.putString("artist", it.getString(artistIndex))
                song.putString("album", it.getString(albumIndex))
                song.putString("url", filePath)

                // Category based on file extension
                song.putString("category", categorizeAudio(filePath))

                val albumArtUri = Uri.parse("content://media/external/audio/albumart")
                val artworkUri = Uri.withAppendedPath(albumArtUri, albumId.toString())
                song.putString("artwork", artworkUri.toString())

                if (artworkQuality != null) {
                    song.putInt("artworkQuality", artworkQuality)
                }

                audioList.pushMap(song)
            }
        }

        return audioList
    }

    // Get all audio files, sorted and with options
    @ReactMethod
    fun getAllAudio(options: ReadableMap, promise: Promise) {
        try {
            val sortBy = if (options.hasKey("sortBy")) options.getString("sortBy") else "TITLE"
            val orderBy = if (options.hasKey("orderBy")) options.getString("orderBy") else "ASC"
            val limit = if (options.hasKey("limit")) options.getInt("limit") else null
            val offset = if (options.hasKey("offset")) options.getInt("offset") else null
            val artworkQuality = if (options.hasKey("artworkQuality")) options.getInt("artworkQuality") else null

            val sortOrder = buildString {
                append("${getColumnName(sortBy ?: "TITLE")} $orderBy")
                if (limit != null) append(" LIMIT $limit")
                if (offset != null) append(" OFFSET $offset")
            }

            val result = querySongs(null, null, sortOrder, artworkQuality)
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("ERROR", e)
        }
    }

    // Get all audio files by album
    @ReactMethod
    fun getSongsByAlbum(promise: Promise) {
        try {
            val contentResolver: ContentResolver = reactContext.contentResolver
            val uri: Uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

            val projection = arrayOf(
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM_ART
            )

            val cursor = contentResolver.query(
                uri,
                projection,
                null,
                null,
                "${MediaStore.Audio.Albums.ALBUM} ASC"
            )

            val albumList = WritableNativeArray()

            cursor?.use {
                val idIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
                val albumIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
                val artistIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
                val coverIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART)

                while (it.moveToNext()) {
                    val album = WritableNativeMap()
                    album.putInt("id", it.getInt(idIndex))
                    album.putString("album", it.getString(albumIndex))
                    album.putString("artist", it.getString(artistIndex))
                    album.putString("artwork", it.getString(coverIndex))

                    albumList.pushMap(album)
                }
            }

            promise.resolve(albumList)
        } catch (e: Exception) {
            promise.reject("ERROR", e)
        }
    }

    // Search songs by title
    @ReactMethod
    fun searchSongsByTitle(options: ReadableMap, promise: Promise) {
        try {
            val title = if (options.hasKey("title")) options.getString("title") else ""
            val selection = "${MediaStore.Audio.Media.TITLE} LIKE ?"
            val selectionArgs = arrayOf("%$title%")

            val result = querySongs(selection, selectionArgs, "${MediaStore.Audio.Media.TITLE} ASC", null)
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("ERROR", e)
        }
    }

    // Helper function to get column name for sorting
    private fun getColumnName(name: String): String {
        return when (name.uppercase()) {
            "TITLE" -> MediaStore.Audio.Media.TITLE
            "ARTIST" -> MediaStore.Audio.Media.ARTIST
            "ALBUM" -> MediaStore.Audio.Media.ALBUM
            "DATE" -> MediaStore.Audio.Media.DATE_ADDED
            else -> MediaStore.Audio.Media.TITLE
        }
    }
}
