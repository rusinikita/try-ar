package com.nikita.tryar.ar

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class GLView : GLSurfaceView {
  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

    setEGLContextClientVersion(2)
    setRenderer(MegaRenderer())
  }
}