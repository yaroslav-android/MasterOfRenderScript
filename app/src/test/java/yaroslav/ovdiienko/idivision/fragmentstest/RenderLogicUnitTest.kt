package yaroslav.ovdiienko.idivision.fragmentstest

import android.content.Context
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import yaroslav.ovdiienko.idivision.fragmentstest.imageprocessing.RenderHelperWrapper
import yaroslav.ovdiienko.idivision.fragmentstest.imageprocessing.RenderScriptView
import yaroslav.ovdiienko.idivision.fragmentstest.imageprocessing.util.C

@RunWith(MockitoJUnitRunner::class)
class RenderLogicUnitTest {
    @Mock
    private lateinit var renderMock: RenderHelperMock
    @Mock
    private lateinit var viewMock: RenderScriptView
    @Mock
    private lateinit var context: Context

    @Test
    fun start_isSuccessful() {
        renderMock.onStart(context, viewMock)
    }

    @Test
    fun calculateValue_isCorrect() {
        Assert.assertEquals(
            1f,
            renderMock.calculateValue_test(50, C.MIN_SATURATION_VAL, C.MAX_SATURATION_VAL)
        )

        Assert.assertEquals(
            0f,
            renderMock.calculateValue_test(50, C.MIN_HUE_VAL, C.MAX_HUE_VAL)
        )
    }

    @Test
    fun bitmapPositionValue_isCorrect() {
        renderMock.swapBitmapPosition_test()
        Assert.assertEquals(0, renderMock.currentBitmap)
    }

    open inner class RenderHelperMock(context: Context?) : RenderHelperWrapper(context) {
        public override var currentBitmap = super.currentBitmap

        fun swapBitmapPosition_test() = swapBitmapPosition()

        fun calculateValue_test(progress: Int, min: Float, max: Float) =
            calculateValue(progress, min, max)
    }
}


