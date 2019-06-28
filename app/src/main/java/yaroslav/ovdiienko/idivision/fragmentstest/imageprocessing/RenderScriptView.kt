package yaroslav.ovdiienko.idivision.fragmentstest.imageprocessing

import android.graphics.Bitmap
import yaroslav.ovdiienko.idivision.fragmentstest.imageprocessing.util.Mode


interface RenderScriptView {
    fun setMode(mode: Mode)
    fun setImageBitmap(bitmap: Bitmap?, shouldInvalidateImage: Boolean)
    fun setupDefaults()
}