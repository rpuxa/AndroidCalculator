package ru.rpuxa.androidcalculator.graph3d

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import ru.rpuxa.androidcalculator.sqr
import java.util.concurrent.CountDownLatch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Graph3dView(context: Context, attributeSet: AttributeSet) :
    SurfaceView(context, attributeSet), SurfaceHolder.Callback {

    init {
        holder.addCallback(this)
        //    setLayerType(View.LAYER_TYPE_SOFTWARE, null)

    }

    fun showGraph(graph: Graph3d) {
        drawThread.setGraph(graph)
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
                return cos(x) * sin(y)
            }
        })
        drawThread.updateCanvas()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        drawThread.changeCamera {
            screenWidth = w.toFloat()
            screenHeight = h.toFloat()

            val cameraWidth = 3f

            cameraHeight = cameraWidth / w * h
            this.cameraWidth = cameraWidth
        }
    }


    private var hAngle = 0f
    private var vAngle = 0f
    private var lastPosX: Float? = null
    private var lastPosY: Float? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                val lastPosX = lastPosX
                val lastPosY = lastPosY
                if (lastPosX != null && lastPosY != null) {
                    hAngle += (lastPosX - event.x) * SENSITIVITY
                    vAngle += (event.y - lastPosY) * SENSITIVITY
                    if (vAngle > PI / 3)
                        vAngle = PI.toFloat() / 3
                    if (vAngle < -PI / 3)
                        vAngle = -PI.toFloat() / 3
                    drawThread.changeCamera {
                        val r = CAMERA_RADIUS * cos(vAngle)
                        x = r * cos(hAngle)
                        y = r * sin(hAngle)
                        z = CAMERA_RADIUS * sin(vAngle)
                        setLookAt(0f, 0f, 0f)
                    }
                    drawThread.updateCanvas()
                }
                this.lastPosY = event.y
                this.lastPosX = event.x
            }
            MotionEvent.ACTION_UP -> {
                lastPosX = null
                lastPosY = null
            }
        }
        return true
    }

    private class DrawThread(private val holder: SurfaceHolder) : Thread("Graph3d Draw Thread") {

        val camera = Camera(
            5f, 5f, 3f,
            0f, 0f,
            3f, 6f,
            0f, 0f,
            3f
        ).apply {
            setLookAt(0f, 0f, 0f)
        }

        inline fun changeCamera(block: Camera.() -> Unit) {
            synchronized(camera) {
                camera.block()
            }
        }

        fun setGraph(graph: Graph3d) {
            synchronized(graphParams) {
                this.graph = graph
                graphChanged = true
            }

        }

        fun updateCanvas() {
            drawLock.countDown()
        }

        private val graphParams = Graph3dParams(30, .2f)
        private var graph: Graph3d? = null
        private var graphChanged = false
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
                            println("$x  $y  $z")
                            points[index++] = x
                            points[index++] = y
                            points[index++] = z
                        }
                    }
                    graphPoints = points
                    graphChanged = false
                }
            }
            /*  graphPoints = floatArrayOf(
                  -1f, -1f, 0f,
                  1f, -1f, 0f,
                  -1f, 1f, 0f,
                  1f, 1f, 0f
              )*/
            val camera = synchronized(camera) { camera.copy() }

            NativeGraph.draw(
                canvas,
                paint,
                camera,
                graphPoints,
                graphParams,
                Canvas.VertexMode.TRIANGLES
            )
        }
    }

    companion object {
        private const val SENSITIVITY = .005f
        private const val CAMERA_RADIUS = 6f
    }
}