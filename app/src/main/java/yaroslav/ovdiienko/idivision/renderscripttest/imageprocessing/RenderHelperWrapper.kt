package yaroslav.ovdiienko.idivision.renderscripttest.imageprocessing

import android.content.Context
import android.graphics.Bitmap
import androidx.renderscript.*
import kotlinx.coroutines.*
import yaroslav.ovdiienko.idivision.renderscripttest.fileutil.FileUtil
import yaroslav.ovdiienko.idivision.renderscripttest.imageprocessing.util.C
import yaroslav.ovdiienko.idivision.renderscripttest.imageprocessing.util.Mode
import yaroslav.ovdiienko.idivision.renderstripttest.ScriptC_test
import kotlin.math.cos
import kotlin.math.sin


open class RenderHelperWrapper(private var context: Context?) : RenderHelper {
    private var view: RenderScriptView? = null

    private val job: Job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private lateinit var script: ScriptC_test
    private lateinit var scriptMatrix: ScriptIntrinsicColorMatrix

    private lateinit var bitmapIn: Bitmap
    private val bitmapOut = arrayOfNulls<Bitmap>(C.BITMAP_COUNT)

    private lateinit var allocationIn: Allocation
    private val allocationOut = arrayOfNulls<Allocation>(C.BITMAP_COUNT)

    protected open var currentBitmap = 0
    private var mode = Mode.HUE

    constructor(context: Context?, mode: Mode) : this(context) {
        this.mode = mode
    }

    override fun onStart(context: Context?, view: RenderScriptView) {
        this.context = context
        this.view = view

        initialSetup()
    }

    private fun initialSetup() {
        val rs = RenderScript.create(context)
        script = ScriptC_test(rs)
        scriptMatrix = ScriptIntrinsicColorMatrix.create(rs, Element.U8_4(rs))

        allocBitmaps()
        view?.setImageBitmap(bitmapOut[currentBitmap], false)
        currentBitmap += (currentBitmap + 1) % C.BITMAP_COUNT

        allocProcessSpace(rs)

        view?.setMode(mode)
        view?.setupDefaults()
    }

    private fun allocBitmaps() {
        (0 until C.BITMAP_COUNT).forEach { i ->
            bitmapOut[i] = Bitmap.createBitmap(bitmapIn.width, bitmapIn.height, bitmapIn.config)
        }
    }

    private fun allocProcessSpace(rs: RenderScript) {
        allocationIn = Allocation.createFromBitmap(rs, bitmapIn)
        (0 until C.BITMAP_COUNT).forEach { i ->
            allocationOut[i] = Allocation.createFromBitmap(rs, bitmapOut[i])
        }
    }

    override fun onBarProgressChanged(progress: Int) {
        val max = when (mode) {
            Mode.HUE -> C.MAX_HUE_VAL
            Mode.SATURATION -> C.MAX_SATURATION_VAL
        }
        val min = when (mode) {
            Mode.HUE -> C.MIN_HUE_VAL
            Mode.SATURATION -> C.MIN_SATURATION_VAL
        }

        doImageTransform(calculateValue(progress, min, max))
    }

    private fun doImageTransform(value: Float) {
        scope.launch {
            val bitmapPosition = withContext(Dispatchers.IO) {
                async { processImage(value) }
            }

            view?.setImageBitmap(bitmapOut[bitmapPosition.await()], true)
        }
    }

    private fun processImage(value: Float): Int {
        val index = currentBitmap

        when (mode) {
            Mode.HUE -> processHUE(value)
            Mode.SATURATION -> processSaturation(value)
        }

        allocationOut[currentBitmap]?.copyTo(bitmapOut[currentBitmap])
        swapBitmapPosition()
        return index
    }

    private fun processHUE(value: Float) {
        val cos = cos(value)
        val sin = sin(value)
        val mat = Matrix3f().also { setupMatrix(it, cos, sin) }

        scriptMatrix.setColorMatrix(mat)
        scriptMatrix.forEach(allocationIn, allocationOut[currentBitmap])
    }

    private fun setupMatrix(matrix: Matrix3f, cos: Float, sin: Float) {
        matrix.apply {
            set(0, 0, (.299 + .701 * cos + .168 * sin).toFloat())
            set(1, 0, (.587 - .587 * cos + .330 * sin).toFloat())
            set(2, 0, (.114 - .114 * cos - .497 * sin).toFloat())
            set(0, 1, (.299 - .299 * cos - .328 * sin).toFloat())
            set(1, 1, (.587 + .413 * cos + .035 * sin).toFloat())
            set(2, 1, (.114 - .114 * cos + .292 * sin).toFloat())
            set(0, 2, (.299 - .3 * cos + 1.25 * sin).toFloat())
            set(1, 2, (.587 - .588 * cos - 1.05 * sin).toFloat())
            set(2, 2, (.114 + .886 * cos - .203 * sin).toFloat())
        }
    }

    private fun processSaturation(value: Float) {
        script._saturationValue = value
        script.forEach_saturation(allocationIn, allocationOut[currentBitmap])
    }

    protected fun calculateValue(progress: Int, min: Float, max: Float): Float {
        return ((max - min) * (progress / 100.0f) + min)
    }

    protected fun swapBitmapPosition() {
        currentBitmap = (currentBitmap + 1) % C.BITMAP_COUNT
    }

    override fun toggleMode() {
        mode = when (mode) {
            Mode.HUE -> Mode.SATURATION
            Mode.SATURATION -> Mode.HUE
        }

        view?.setMode(mode)
    }

    override fun saveBitmap(bitmap: Bitmap) {
        scope.launch {
            val savingProcess = withContext(Dispatchers.IO) {
                async { FileUtil.saveImageToStorage(bitmap) }
            }

            savingProcess.start()
        }
    }

    override fun loadBitmap(bitmap: Bitmap) {
        bitmapIn = bitmap
    }

    override fun onStop() {
        context = null
        view = null
    }
}