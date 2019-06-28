package yaroslav.ovdiienko.idivision.fragmentstest.imageprocessing

import android.content.Context
import android.graphics.Bitmap


interface RenderHelper {
    fun onBarProgressChanged(progress: Int)
    fun loadBitmap(bitmap: Bitmap)

    fun toggleMode()

    fun onStart(context: Context?, view: RenderScriptView)
    fun onStop()
}