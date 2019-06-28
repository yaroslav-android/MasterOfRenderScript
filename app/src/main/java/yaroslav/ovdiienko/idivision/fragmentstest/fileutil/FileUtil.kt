package yaroslav.ovdiienko.idivision.fragmentstest.fileutil

import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.util.*


object FileUtil {
    private const val QUALITY = 85
    private const val FOLDER = "MasterOfRenderScript"
    private val STORAGE_PATH = "${Environment.getExternalStorageDirectory()}/$FOLDER"
    private val FILE_PATH = "rendered-image-" + UUID.randomUUID().toString() + ".png"

    fun saveImageToStorage(bitmap: Bitmap) {
        val dir = File(STORAGE_PATH)
        dir.createDirIfNotExist()
        val file = File(dir, FILE_PATH)
        file.createFileIfNotExist()

        FileOutputStream(file).use { output ->
            output.run {
                bitmap.compress(Bitmap.CompressFormat.PNG, QUALITY, this)
                flush()
            }
        }
    }
}

internal fun File.createDirIfNotExist() {
    if (!exists()) mkdirs()
}

internal fun File.createFileIfNotExist() {
    if (!exists()) createNewFile()
}