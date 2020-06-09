package `fun`.inaction.transfer.customviews

import `fun`.inaction.transfer.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView

class PointTextView : AppCompatTextView {

    private val paint: Paint

    /**
     * 圆点颜色
     */
    var pointColor:Int = Color.parseColor("#6200EA")

    private var pointRadius:Float = 10f
    private var space:Int = 40
    init {
        paint = Paint()
        paint.isAntiAlias = true
        paint.color = pointColor
        setPadding((space+pointRadius*2).toInt(),0,0,0)
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawCircle(pointRadius,measuredHeight/2f,pointRadius,paint)
    }

    /**
     * 设置全部颜色
     */
    fun setColor(color:Int){
        paint.color = color
        setTextColor(color)

        invalidate()
    }

}