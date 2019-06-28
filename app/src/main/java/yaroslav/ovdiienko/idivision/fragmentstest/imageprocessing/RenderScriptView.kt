package yaroslav.ovdiienko.idivision.fragmentstest.imageprocessing

import android.graphics.Bitmap


interface RenderScriptView {
    fun setMode(mode: Mode)
    fun setImageBitmap(bitmap: Bitmap?, shouldInvalidateImage: Boolean)
    fun setupDefaults()
}