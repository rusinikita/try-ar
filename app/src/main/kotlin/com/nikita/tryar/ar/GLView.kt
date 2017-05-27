package com.nikita.tryar.ar

import android.content.ContentValues.TAG
import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay


class GLView : GLSurfaceView {
  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

    setEGLContextFactory(ContextFactory())
  }

  private class ContextFactory : GLSurfaceView.EGLContextFactory {
    private val EGL_CONTEXT_CLIENT_VERSION = 0x3098

    override fun createContext(egl: EGL10, display: EGLDisplay, eglConfig: EGLConfig): EGLContext {
      Log.w(TAG, "creating OpenGL ES 3.1 context")
      val attrib_list = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE)
      // attempt to create a OpenGL ES 3.1 context
      val context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list)
      return context // returns null if 3.1 is not supported;
    }

    override fun destroyContext(egl: EGL10, display: EGLDisplay, context: EGLContext) {
      egl.eglDestroyContext(display, context)
    }
  }
}