package com.esmaeel.esviewsfactory

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import kotlinx.android.synthetic.main.es_rangebar_layout.view.*

/**
 * TODO: document your custom view class.
 */
class EsRangebar : LinearLayout {

     var startRange = 0
     var endRange = 1000
     private var leftSeekBar: com.jaygoo.widget.SeekBar? = null
     private var rightSeekBar: com.jaygoo.widget.SeekBar? = null
    var indicatorView : View? = null
    var isRightDrag = false
    var minValue = 0f
    var maxValue = 1000f
    var padding = 0
    var indicatorMinValue = minValue - minValue
    var indicatorMaxValue = maxValue - minValue

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        LayoutInflater.from(context).inflate(R.layout.es_rangebar_layout, this)
    }


    fun doTheMagicIn(context: Context , indicatorLayoutStart : Int = R.layout.indicator_start,indicatorLayoutEnd : Int = R.layout.indicator_end, originalMinValue: Int = 1000, originalMaxValue: Int = 20000){
        LayoutInflater.from(context).inflate(R.layout.es_rangebar_layout, this)
        startBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (isRightDrag) {
                    indicatorView = LayoutInflater.from(context).inflate(indicatorLayoutEnd, null, false)
                    endRange = i
                    seekBar.thumb = getThumb(i, indicatorView!!)
                    if (endRange in (indicatorMinValue + 1)..indicatorMaxValue) {
                        rangeBar.setValue(startRange.toFloat(), endRange.toFloat())
                    }
                } else {
                    indicatorView = LayoutInflater.from(context).inflate(indicatorLayoutStart, null, false)
                    startRange = i
                    seekBar.thumb = getThumb(i, indicatorView!!)
                    if (startRange in (indicatorMinValue + 1)..indicatorMaxValue) {
                        rangeBar.setValue(startRange.toFloat(), endRange.toFloat())
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
        rangeBar.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(view: RangeSeekBar, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                Log.d("***PROGRESS***", "$leftValue - $rightValue")
                if (isRightDrag) {
                    if (rightValue.toInt() > leftValue) {
                        startBar.progress = rightValue.toInt()
                        startBar.post {
                            if (rightValue.toInt() - indicatorMinValue > (indicatorMinValue + indicatorMaxValue)/4) {
                                startBar.setPadding(0, 0, (padding * 1.5).toInt(), 0)
                            } else {
                                startBar.setPadding(padding, 0, (padding * 1.5).toInt(), 0)
                            }
                        }
                    }
                } else {
                    if (rightValue.toInt() > leftValue) {
                        startBar.progress = leftValue.toInt()
                        startBar.post {
                            if (indicatorMaxValue - leftValue.toInt() > (indicatorMinValue + indicatorMaxValue)/4) {
                                startBar.setPadding(padding, 0, (padding * 1.5).toInt(), 0)
                            } else {
                                startBar.setPadding(padding, 0, (padding * 1.5).toInt(), 0)
                            }
                        }
                    }
                }
                startBar.visibility = View.VISIBLE
            }

            override fun onStartTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {
                isRightDrag = !isLeft
            }
            override fun onStopTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {
                isRightDrag = !isLeft
                startBar.visibility = View.INVISIBLE
            }
        })

        rangeBar.seekBarMode = RangeSeekBar.SEEKBAR_MODE_RANGE


        leftSeekBar = rangeBar.leftSeekBar
        rightSeekBar = rangeBar.rightSeekBar

        leftSeekBar!!.setIndicatorTextStringFormat("$ %s")
        leftSeekBar!!.setIndicatorTextDecimalFormat("0")
        rightSeekBar!!.setIndicatorTextStringFormat("$ %s")
        rightSeekBar!!.setIndicatorTextDecimalFormat("0")

        isRightDrag = false
        indicatorView = if (isRightDrag) {
            LayoutInflater.from(context).inflate(indicatorLayoutEnd, null, false)
        } else {
            LayoutInflater.from(context).inflate(indicatorLayoutStart, null, false)
        }
        initializeRanges(originalMinValue, originalMaxValue)
        // to invalidate the change of the thumb after the first initialization
//        startBar.progress = startBar.progress + 1
//        endBar.progress = endBar.progress + 1
    }

    private fun initializeRanges(originalMinValue: Int, originalMaxValue: Int) {
        maxValue = originalMaxValue.toFloat()
        minValue = originalMinValue.toFloat()
        indicatorMinValue = minValue - minValue
        indicatorMaxValue = maxValue - minValue
        startRange = indicatorMinValue.toInt()
        endRange = indicatorMaxValue.toInt()
        startBar.max = indicatorMaxValue.toInt()
        rangeBar.setRange(indicatorMinValue,indicatorMaxValue)
        rangeBar.setValue(indicatorMinValue, indicatorMaxValue)
        startBar.progress = indicatorMinValue.toInt()
        rangeBar.postInvalidate()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        val padding = indicatorView!!.width / 2
//        startBar.max = 1000
//        endBar.max = 1000
//        rangeBar.setRange(0f,1000f)
//        startBar.setPadding(padding / 2, 0, padding, 0)
//        endBar.setPadding(padding / 2, 0, padding, 0)
//        rangeBar.setPadding(padding / 2, 0, padding, 0)
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        padding = indicatorView!!.width / 2
        initializeRanges(minValue.toInt(), maxValue.toInt())
        rangeBar.setPadding(padding , 0, padding, 0)
        startBar.visibility = View.INVISIBLE
    }

    fun getThumb(progress: Int, indicator: View): Drawable {
        val trueProgress: Int = (progress+minValue).toInt()
        (indicator.findViewById<View>(R.id.progress_text) as TextView).text = "$trueProgress"

        indicator.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(indicator.measuredWidth, indicator.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        indicator.layout(0, 0, indicator.measuredWidth, indicator.measuredHeight)
        indicator.draw(canvas)

        return BitmapDrawable(resources, bitmap)
    }

}

