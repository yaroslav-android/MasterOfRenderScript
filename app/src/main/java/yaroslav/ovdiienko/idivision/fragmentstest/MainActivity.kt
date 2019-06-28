package yaroslav.ovdiienko.idivision.fragmentstest

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import yaroslav.ovdiienko.idivision.fragmentstest.imageprocessing.Mode
import yaroslav.ovdiienko.idivision.fragmentstest.imageprocessing.RenderHelper
import yaroslav.ovdiienko.idivision.fragmentstest.imageprocessing.RenderHelperWrapper
import yaroslav.ovdiienko.idivision.fragmentstest.imageprocessing.RenderScriptView


class MainActivity : AppCompatActivity(), RenderScriptView {
    private lateinit var image: ImageView
    private lateinit var seekBar: SeekBar
    private lateinit var changeModeButton: Button

    private lateinit var renderHelper: RenderHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        renderHelper = RenderHelperWrapper(this)

        findViews()
        setupViewListeners()
    }

    private fun findViews() {
        image = iv_renderscript
        seekBar = seek_bar
        changeModeButton = change_mode
        renderHelper.loadBitmap(getBitmap(R.drawable.img_lights))
    }

    private fun setupViewListeners() {
        seekBar.setOnSeekBarChangeListener(getSeekBarListener())
        changeModeButton.setOnClickListener { renderHelper.toggleMode() }
    }

    private fun getSeekBarListener(): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) renderHelper.onBarProgressChanged(progress)
            }

            /* No need to use methods below */

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }
    }

    override fun setMode(mode: Mode) {
        changeModeButton.text = when (mode) {
            Mode.HUE -> HUE_STRING
            Mode.SATURATION -> SATURATION_STRING
        }
    }

    override fun onStart() {
        super.onStart()
        renderHelper.onStart(this, this)
    }

    override fun onStop() {
        renderHelper.onStop()
        super.onStop()
    }

    override fun setupDefaults() {
        val progress = 50
        renderHelper.onBarProgressChanged(progress)
        seekBar.progress = progress
        changeModeButton.text = HUE_STRING
    }

    override fun setImageBitmap(bitmap: Bitmap?, shouldInvalidateImage: Boolean) {
        image.setImageBitmap(bitmap)
        if (shouldInvalidateImage) image.invalidate()
    }

    private fun getBitmap(resource: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        return BitmapFactory.decodeResource(resources, resource, options)
    }

    companion object {
        private const val HUE_STRING = "HUE"
        private const val SATURATION_STRING = "SATURATION"
    }
}
