package ru.rpuxa.androidcalculator.graph3d

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.*
import ru.rpuxa.androidcalculator.sqr
import java.util.concurrent.CountDownLatch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sin

class Graph3dView(context: Context, attributeSet: AttributeSet) :
    SurfaceView(context, attributeSet), SurfaceHolder.Callback {

    private val gestureListener = GestureListener()
    private val gestureDetector = GestureDetector(context, gestureListener)
    private val scaleGestureDetector = ScaleGestureDetector(context, gestureListener)

    init {
        holder.addCallback(this)
        //    setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    fun showGraph(graph: Graph3d) {
        synchronized(drawThread.graphParams) {
            drawThread.graph = graph
            drawThread.graphChanged = true
        }
    }

    private val drawThread = DrawThread(holder)

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        drawThread.interrupt()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        drawThread.start()
        showGraph(object : Graph3d {
            override fun calculate(x: Float, y: Float): Float {
                return ln(sqr(x) + sqr(y))
            }
        })
        gestureListener.onChange()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val camera = drawThread.camera
        synchronized(camera) {
            camera.screenWidth = w.toFloat()
            camera.screenHeight = h.toFloat()
            camera.cameraWidth = CAMERA_WIDTH
            camera.cameraHeight = CAMERA_WIDTH / w * h
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        scaleGestureDetector.onTouchEvent(event)
        return true
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener(),
        ScaleGestureDetector.OnScaleGestureListener {

        private var radius = CAMERA_RADIUS
        private var horizontalAngle = 0f
        private var verticalAngle = 0f

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            horizontalAngle += distanceX * SENSITIVITY
            verticalAngle -= distanceY * SENSITIVITY
            if (verticalAngle > PI / 2)
                verticalAngle = PI.toFloat() / 2
            if (verticalAngle < -PI / 2)
                verticalAngle = -PI.toFloat() / 2
            onChange()
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
            val params = drawThread.graphParams
            synchronized(params) {
                params.polygonSize = radius / 100
                drawThread.graphChanged = true
            }
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            radius /= detector.scaleFactor
            onChange()
            return true
        }

        fun onChange() {
            val camera = drawThread.camera
            synchronized(camera) {
                val radius2 = radius * cos(verticalAngle)
                camera.x = radius2 * cos(horizontalAngle)
                camera.y = radius2 * sin(horizontalAngle)
                camera.z = radius * sin(verticalAngle)
                camera.setLookAt(0f, 0f, 0f)
            }
            drawThread.updateCanvas()
        }
    }

    private class DrawThread(private val holder: SurfaceHolder) : Thread("Graph3d Draw Thread") {

        val camera = Camera(
            0f, 0f, 0f,
            0f, 0f,
            0f, 0f,
            0f, 0f,
            3f
        )


        fun updateCanvas() {
            drawLock.countDown()
        }

        val graphParams = Graph3dParams(50, .1f)
        var graph: Graph3d? = null
        var graphChanged = false
        private lateinit var graphPoints: FloatArray


        private val paint = Paint()

        private var drawLock = CountDownLatch(1)

        override fun run() {
            while (!isInterrupted) {
                try {
                    drawLock.await()
                    drawLock = CountDownLatch(1)
                    val canvas = holder.lockCanvas()
                    drawGraph(canvas)
                    holder.unlockCanvasAndPost(canvas)
                } catch (e: InterruptedException) {
                    return
                }
            }
        }

        private fun drawGraph(canvas: Canvas) {
            synchronized(graphParams) {
                val graph = graph ?: return

                if (graphChanged) {
                    val count = graphParams.polygonsCount
                    val size = graphParams.polygonSize
                    val points = FloatArray(sqr(count + 1) * 3)
                    var index = 0
                    val start = graphParams.polygonStart
                    repeat(count + 1) { dx ->
                        repeat(count + 1) { dy ->
                            val x = start + dx * size
                            val y = start + dy * size
                            val z = graph.calculate(x, y)
                            points[index++] = x
                            points[index++] = y
                            points[index++] = z
                        }
                    }
                    graphPoints = points
                    graphChanged = false
                }
            }
            val camera = synchronized(camera) { camera.copy() }
            NativeGraph.draw(
                canvas,
                paint,
                camera,
                graphPoints.copyOf(),
                graphParams,
                Canvas.VertexMode.TRIANGLES
            )


        }
    }

    companion object {
        private const val SENSITIVITY = .005f
        private const val CAMERA_RADIUS = 10f
        private const val CAMERA_WIDTH = 3f
    }
}