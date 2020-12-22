package com.ovpark.openglstudydemo

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ovpark.openglstudydemo.shape.TriangleShapeRender

class MainActivity : AppCompatActivity() {

    /**
     * 是否已经设置过render
     */
    private var isRenderSet = false

    /**
     * 当前显示的gl Surface
     */
    private var glSurfaceView: GLSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (GLESUtils.isSupportEs2(this)) {
            //创建一个GLSurfaceView
            glSurfaceView = GLSurfaceView(this)
            glSurfaceView?.setEGLContextClientVersion(2)
            //设置自己的Render.Render 内进行图形的绘制
            glSurfaceView?.setRenderer(TriangleShapeRender(this))
            isRenderSet = true
            setContentView(glSurfaceView)
        } else {
            Toast.makeText(
                this,
                "This device does not support OpenGL ES 2.0!!!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isRenderSet) {
            glSurfaceView?.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isRenderSet) {
            glSurfaceView?.onResume()
        }
    }
}