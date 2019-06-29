package yaroslav.ovdiienko.idivision.renderscripttest.imageprocessing

import android.graphics.Bitmap
import yaroslav.ovdiienko.idivision.renderscripttest.imageprocessing.util.Mode


interface RenderScriptView {
    fun setMode(mode: Mode)
    fun setImageBitmap(bitmap: Bitmap?, shouldInvalidateImage: Boolean)
    fun setupDefaults()
}