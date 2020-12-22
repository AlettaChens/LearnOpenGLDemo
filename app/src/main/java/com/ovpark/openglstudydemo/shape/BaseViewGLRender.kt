package com.ovpark.openglstudydemo.shape

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 主要在Render类内，完成对应的绘制.
 * 对应的生命周期的回调:
 * -> onSurfaceCreated->  GLSurfaceView创建的时机。按照惯用思维，在这里进行一些初始化的操作。
 * -> onSurfaceChanged-> GLSurfaceView改变的时机。
 * -> onDrawFrame-> 这个生命周期方法会不断的回调。不断的绘制。
 */
open class BaseViewGLRender : GLSurfaceView.Renderer {
    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        //0.简单的给窗口填充一种颜色
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        //在创建的时候，去创建这些着色器
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        //在窗口改变的时候调用
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        //0.glClear（）的唯一参数表示需要被清除的缓冲区。当前可写的颜色缓冲
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    }
}