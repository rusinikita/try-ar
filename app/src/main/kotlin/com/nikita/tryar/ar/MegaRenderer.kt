package com.nikita.tryar.ar

import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MegaRenderer(private val layers: List<RenderLayer>) : GLSurfaceView.Renderer {

  override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {

  }

  override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {

  }

  override fun onDrawFrame(gl: GL10) {

  }

  class RenderLayer {

    fun onSurfaceCreated(gl: GL10, config: EGLConfig) {

    }

    fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {

    }

    fun onDrawFrame(gl: GL10) {

    }
  }
}