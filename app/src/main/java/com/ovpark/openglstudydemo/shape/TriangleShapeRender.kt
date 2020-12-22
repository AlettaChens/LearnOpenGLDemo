package com.ovpark.openglstudydemo.shape

import android.content.Context
import android.opengl.GLES20
import com.ovpark.openglstudydemo.Constant
import com.ovpark.openglstudydemo.GLESUtils.Companion.compileShaderCode
import com.ovpark.openglstudydemo.GLESUtils.Companion.readAssetShaderCode
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 简单的三角形的形状的render
 *
 * 步骤:
 * 1. 将着色器代码进行编译得到 program的Id.
 * 2. vertex着色器上定义了 attribute 的vPosition 赋值给了 gl_Position.
 * 3. fragment着色器上定义了 常量uniform的uColor 赋值给 gl_FragColor.
 * 4. 定义描述形状的定点。OpenGL中的每个图形都是由这样子的顶点来完成的.
 */
class TriangleShapeRender(private val context: Context) : BaseViewGLRender() {
    private var mProgramObjectId = 0 //pragramId
    private val mVertexFloatBuffer: FloatBuffer//顶点数据的内存映射

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        super.onSurfaceCreated(gl, config)
        //0.先去得到着色器的代码
        val vertexShaderCode = readAssetShaderCode(context, VERTEX_SHADER_FILE)
        val fragmentShaderCode = readAssetShaderCode(context, FRAGMENT_SHADER_FILE)
        //1.得到之后，进行编译。得到id
        val vertexShaderObjectId = compileShaderCode(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShaderObjectId =
            compileShaderCode(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        //3.取得到program的指针
        mProgramObjectId = GLES20.glCreateProgram()
        //将shaderId绑定到program当中
        GLES20.glAttachShader(mProgramObjectId, vertexShaderObjectId)
        GLES20.glAttachShader(mProgramObjectId, fragmentShaderObjectId)
        //4.启动GL link program
        GLES20.glLinkProgram(mProgramObjectId)
        //5.使用program
        GLES20.glUseProgram(mProgramObjectId)
    }


    //在OnDrawFrame中进行绘制
    override fun onDrawFrame(gl: GL10) {
        super.onDrawFrame(gl)
        //1.根据我们定义取出定义的位置
        val vPosition = GLES20.glGetAttribLocation(mProgramObjectId, A_POSITION)
        //2.开始启用我们的position
        GLES20.glEnableVertexAttribArray(vPosition)
        //3.将坐标数据放入
        GLES20.glVertexAttribPointer(
            vPosition,  //上面得到的id
            COORDS_PER_VERTEX,  //告诉他用几个偏移量来描述一个顶点
            GLES20.GL_FLOAT, false,
            STRIDE,  //一个顶点需要多少个字节的偏移量
            mVertexFloatBuffer
        )
        //4.取出颜色
        val uColor = GLES20.glGetUniformLocation(mProgramObjectId, U_COLOR)
        //5.设置绘制三角形的颜色
        GLES20.glUniform4fv(uColor, 1, TRIANGLE_COLOR, 0)
        //6.绘制三角形.
        //glDrawArrays的几种方式 GL_TRIANGLES三角形 GL_TRIANGLE_STRIP三角形带的方式(开始的3个点描述一个三角形，后面每多一个点，多一个三角形) GL_TRIANGLE_FAN扇形(可以描述圆形)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, VERTEX_COUNT)
        //7.禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(vPosition)
    }

    companion object {
        private const val VERTEX_SHADER_FILE = "shape/triangle_vertex_shader.glsl" //定点着色器位置
        private const val FRAGMENT_SHADER_FILE = "shape/triangle_fragment_shader.glsl" //片元着色器位置
        private const val A_POSITION = "aPosition" //获取顶点位置TAG
        private const val U_COLOR = "uColor" //获取颜色TAG
        private const val COORDS_PER_VERTEX = 3 //一个顶点需要3个偏移量(x,y,z)。
        private const val COORDS_PER_COLOR = 0 //固定颜色不考虑颜色偏移量。
        private const val TOTAL_COMPONENT_COUNT =
            COORDS_PER_VERTEX + COORDS_PER_COLOR //描述一个顶点，总共的顶点需要的偏移量。
        private const val STRIDE =
            TOTAL_COMPONENT_COUNT * Constant.BYTES_PER_FLOAT //描述一个顶点需要的byte偏移量。

        //顶点的坐标系  X, Y, Z
        private val TRIANGLE_COORDS = floatArrayOf(
            0.5f, 0.5f, 0.0f,  // top
            -0.5f, -0.5f, 0.0f,  // bottom left
            0.5f, -0.5f, 0.0f // bottom right
        )
        private val TRIANGLE_COLOR = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f) //设置固定的颜色,依次为红绿蓝和透明通道。
        private val VERTEX_COUNT = TRIANGLE_COORDS.size / TOTAL_COMPONENT_COUNT
    }

    init {
        /*0. 调用GLES20的包的方法时，其实就是调用JNI的方法。
        所以分配本地的内存块，将java数据复制到本地内存中，而本地内存可以不受垃圾回收的控制
        1. 使用nio中的ByteBuffer来创建内存区域。
        2. ByteOrder.nativeOrder()来保证，同一个平台使用相同的顺序
        3. 然后可以通过put方法，将内存复制过去。
        因为这里是Float，所以就使用floatBuffer
         */
        mVertexFloatBuffer = ByteBuffer
            .allocateDirect(TRIANGLE_COORDS.size * Constant.BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(TRIANGLE_COORDS)
        mVertexFloatBuffer.position(0)
    }
}