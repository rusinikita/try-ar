package com.nikita.tryar.ar

import android.app.Activity
import android.opengl.GLSurfaceView
import com.nikita.ar.from_sample.SampleApplicationControl
import com.nikita.ar.from_sample.SampleApplicationException
import com.vuforia.State
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ARDelegate(val activity: Activity, val glView: GLView) : SampleApplicationControl {

  fun onCreate() {
    glView.setRenderer(object : GLSurfaceView.Renderer {
      override fun onDrawFrame(gl: GL10?) {

      }

      override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

      }

      override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

      }
    })
  }

  fun onResume() {
    glView.onResume()
  }

  fun onPause() {
    glView.onPause()
  }

  fun onDestroy() {

  }

  override fun doInitTrackers(): Boolean {
    return false
  }

  override fun doLoadTrackersData(): Boolean {
    return false
  }

  override fun doStartTrackers(): Boolean {
    return false
  }

  override fun doStopTrackers(): Boolean {
    return false
  }

  override fun doUnloadTrackersData(): Boolean {
    return false
  }

  override fun doDeinitTrackers(): Boolean {
    return false
  }

  override fun onInitARDone(e: SampleApplicationException?) {

  }

  override fun onVuforiaUpdate(state: State?) {

  }
}