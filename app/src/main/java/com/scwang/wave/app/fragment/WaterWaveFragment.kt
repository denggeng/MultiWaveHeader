package com.scwang.wave.app.fragment


import android.content.res.Resources
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.larswerkman.lobsterpicker.OnColorListener
import com.scwang.wave.app.R
import com.scwang.wave.app.util.StatusBarUtil
import kotlinx.android.synthetic.main.fragment_water_wave.*
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar

/**
 * A simple [Fragment] subclass.
 */
class WaterWaveFragment : Fragment(), DiscreteSeekBar.OnProgressChangeListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_water_wave, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtil.immersive(activity)
        StatusBarUtil.setPaddingSmart(context, toolbar2)
        toolbar2.setNavigationOnClickListener {
            activity.finish()
        }

        seekAngle2.progress = waterWave.gradientAngle
        seekVelocity2.progress = (waterWave.velocity * 10).toInt()
        seekAlpha2.progress = (waterWave.colorAlpha * 100).toInt()
        seekProgress2.progress = (waterWave.progress * 100).toInt()
        seekAmplitude2.progress = (waterWave.amplitude / Resources.getSystem().displayMetrics.density).toInt()
        seekCrest2.progress = 2
        checkBoxRunning2.isChecked = waterWave.isRunning
        checkBoxDirection2.isChecked = waterWave.scaleY == -1f

        seekAmplitude2.setOnProgressChangeListener(this)
        seekCrest2.setOnProgressChangeListener(this)
        seekAngle2.setOnProgressChangeListener(this)
        seekVelocity2.setOnProgressChangeListener(this)
        seekProgress2.setOnProgressChangeListener(this)
        seekAlpha2.setOnProgressChangeListener(this)
        seekNumber2.setOnProgressChangeListener(this)
        checkBoxRunning2.setOnCheckedChangeListener({ _, value ->
            if (value) {
                waterWave.start()
            } else {
                waterWave.stop()
            }
        })
        checkBoxDirection2.setOnCheckedChangeListener({ _, value ->
            waterWave.scaleY = if (value) -1f else 1f
            toolbar2.setBackgroundColor(if (value) ContextCompat.getColor(context, R.color.colorPrimary) else 0)
        })

        sliderStartColor2.addOnColorListener(object : OnColorListener {
            override fun onColorChanged(color: Int) {
                onColorSelected(color)
            }

            override fun onColorSelected(color: Int) {
                waterWave.startColor = color
            }
        })
        sliderCloseColor2.addOnColorListener(object : OnColorListener {
            override fun onColorChanged(color: Int) {
                onColorSelected(color)
            }

            override fun onColorSelected(color: Int) {
                waterWave.closeColor = color
            }
        })

    }

    override fun onProgressChanged(seekBar: DiscreteSeekBar, value: Int, fromUser: Boolean) {
        when (seekBar) {
            seekProgress2 -> waterWave.progress = 1f * value / 100
            seekVelocity2 -> waterWave.velocity = 1f * value / 10
            seekAlpha2 -> waterWave.colorAlpha = 1f * value / 100
            seekAngle2 -> waterWave.gradientAngle = value
            seekCrest2 -> waterWave.crest = value

        }
    }

    override fun onStartTrackingTouch(seekBar2: DiscreteSeekBar) {
    }

    override fun onStopTrackingTouch(seekBar2: DiscreteSeekBar) {
        if (seekAmplitude2 == seekBar2) {
            waterWave.amplitude = seekBar2.progress
        } else if (seekNumber2 == seekBar2) {
            if (seekBar2.progress == 2) {
                /**
                 * 格式-format
                 * offsetX offsetY scaleX scaleY velocity（dp/s）
                 * 水平偏移量 竖直偏移量 水平拉伸比例 竖直拉伸比例 速度
                 */
                waterWave.setWaves("0,0,1,1,25\n90,0,1,1,-25")
            } else {
                val waves = "70,25,1.4,1.4,-26\n100,5,1.4,1.2,15\n420,0,1.15,1,-10\n520,10,1.7,1.5,20\n220,0,1,1,-15".split("\n")
                waterWave.setWaves(waves.subList(0, seekBar2.progress).joinToString("\n"))
            }
        }
    }
}
