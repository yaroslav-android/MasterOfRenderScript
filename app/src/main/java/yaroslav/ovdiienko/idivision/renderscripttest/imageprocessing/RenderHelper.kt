package yaroslav.ovdiienko.idivision.renderscripttest.imageprocessing

import android.content.Context
import android.graphics.Bitmap


interface RenderHelper {
    fun onBarProgressChanged(progress: Int)
    fun loadBitmap(bitmap: Bitmap)
    fun saveBitmap(bitmap: Bitmap)

    fun toggleMode()

    fun onStart(context: Context?, view: RenderScriptView)
    fun onStop()
}