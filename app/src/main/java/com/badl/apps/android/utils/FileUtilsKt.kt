package com.badl.apps.android.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import java.io.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

object FileUtilsKt {

    private const val EOF = -1
    private const val DEFAULT_BUFFER_SIZE = 1024 * 4
    @Throws(IOException::class)
    fun from(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = getFileName(context, uri)
        val splitName = splitFileName(fileName)
        var tempFile = File.createTempFile(splitName[0], splitName[1], context.cacheDir)
        tempFile = rename(tempFile, fileName)
        tempFile.deleteOnExit()
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(tempFile)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        if (inputStream != null) {
            copy(inputStream, out)
            inputStream.close()
        }
        out?.close()
        return tempFile
    }

    private fun splitFileName(fileName: String?): Array<String?> {
        var name = fileName
        var extension: String? = ""
        val i = fileName!!.lastIndexOf(".")
        if (i != -1) {
            name = fileName.substring(0, i)
            extension = fileName.substring(i)
        }
        return arrayOf(name, extension)
    }

    fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf(File.separator)
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    private fun rename(file: File, newName: String?): File {
        val newFile = File(file.parent, newName)
        if (newFile != file) {
            if (newFile.exists() && newFile.delete()) {
                Log.d("FileUtil", "Delete old $newName file")
            }
            if (file.renameTo(newFile)) {
                Log.d("FileUtil", "Rename file to $newName")
            }
        }
        return newFile
    }

    @Throws(IOException::class)
    private fun copy(input: InputStream, output: OutputStream?): Long {
        var count: Long = 0
        var n: Int
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        while (EOF != input.read(buffer).also { n = it }) {
            output!!.write(buffer, 0, n)
            count += n.toLong()
        }
        return count
    }

    public fun getStringSizeLengthFile(size: Long): String {
        val symbols = DecimalFormatSymbols(Locale.US)
        val df = DecimalFormat("0.00", symbols)
        val sizeKb = 1024.0f
        val sizeMb = sizeKb * sizeKb
        val sizeGb = sizeMb * sizeKb
        val sizeTerra = sizeGb * sizeKb
        return when {
            size < sizeMb -> {
                String.format(Locale.US, df.format(size / sizeKb) + " Kb")
            }
            size < sizeGb -> {
                String.format(Locale.US, df.format(size / sizeMb) + " Mb")
            }
            size < sizeTerra -> {
                String.format(Locale.US, df.format(size / sizeGb) + " Gb")

            }
            else -> ""
        }
    }
}