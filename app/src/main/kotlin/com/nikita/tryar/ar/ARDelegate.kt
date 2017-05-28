package com.nikita.tryar.ar

import android.app.Activity
import android.content.pm.ActivityInfo
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.nikita.ar.from_sample.SampleApplicationControl
import com.nikita.ar.from_sample.SampleApplicationException
import com.nikita.ar.from_sample.SampleApplicationSession
import com.nikita.ar.from_sample.utils.Texture
import com.vuforia.*
import com.vuforia.CameraDevice.CAMERA_DIRECTION
import java.util.*

class ARDelegate(private val activity: Activity,
                 private val glViewContainer: ViewGroup,
                 private val progress: View,
                 private val switchButton: FloatingActionButton) : SampleApplicationControl {
  private val vuforiaAppSession = SampleApplicationSession(this)
  private val textures = Vector<Texture>()
  private var glView: GLView? = null
  private var renderer: MegaRenderer? = null
  private var currentDataset: DataSet? = null
  private var isAr = false
  private var needSwitch = false

  fun onCreate() {
    progress.visibility = View.VISIBLE
    switchButton.setOnClickListener {
      isAr = !isAr
      needSwitch = true
    }

    vuforiaAppSession.initAR(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    loadTextures()
  }

  // We want to load specific textures from the APK, which we will later use
  // for rendering.
  private fun loadTextures() {
    textures.add(Texture.loadTextureFromApk("vumark_texture.png", activity.assets))
    textures.add(Texture.loadTextureFromApk("Buildings.jpeg", activity.assets))
  }

  // Initializes AR application components.
  private fun initApplicationAR() {
    glView = GLView(activity)
    renderer = MegaRenderer(activity, vuforiaAppSession)
    renderer?.textures = textures
    glView?.setRenderer(renderer)
  }


  fun onResume() {
    try {
      vuforiaAppSession.resumeAR()
    } catch (e: SampleApplicationException) {
      Log.e("", e.string)
    }
    glView?.onResume()
  }

  fun onPause() {
    glView?.onPause()
    try {
      vuforiaAppSession.pauseAR()
    } catch (e: SampleApplicationException) {
      Log.e("", e.string)
    }
  }

  fun onDestroy() {
    try {
      vuforiaAppSession.stopAR()
    } catch (e: SampleApplicationException) {
      Log.e("", e.string)
    }
    textures.clear()
    System.gc()
  }

  override fun doInitTrackers(): Boolean {
    val trackerManager = TrackerManager.getInstance()
    val tracker = trackerManager.initTracker(ObjectTracker.getClassType())
    if (tracker == null) {
      Log.e("", "Tracker not initialized. Tracker already initialized or the camera is already started")
      return false
    } else {
      Log.i("", "Tracker successfully initialized")
    }
    Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS.toLong(), 10)
    return true
  }

  override fun doLoadTrackersData(): Boolean {
    val trackerManager = TrackerManager.getInstance()
    val objectTracker = trackerManager.getTracker(ObjectTracker.getClassType()) as? ObjectTracker ?: return false

    if (currentDataset == null) {
      currentDataset = objectTracker.createDataSet()
    }

    if (currentDataset == null) return false

    if (!currentDataset!!.load(if (isAr) "StonesAndChips.xml" else "Vuforia.xml", STORAGE_TYPE.STORAGE_APPRESOURCE)) return false

    if (!objectTracker.activateDataSet(currentDataset)) return false

    return true
  }

  override fun doStartTrackers(): Boolean {
    TrackerManager.getInstance().getTracker(ObjectTracker.getClassType())?.start()
    return true
  }

  override fun doStopTrackers(): Boolean {
    TrackerManager.getInstance().getTracker(ObjectTracker.getClassType())?.stop()
    return true
  }

  override fun doUnloadTrackersData(): Boolean {
    // Indicate if the trackers were unloaded correctly
    var result = true

    val tManager = TrackerManager.getInstance()
    val objectTracker = tManager.getTracker(ObjectTracker.getClassType()) as? ObjectTracker ?: return false

    if (currentDataset != null && currentDataset!!.isActive) {
      if (objectTracker.getActiveDataSet(0) == currentDataset && !objectTracker.deactivateDataSet(currentDataset)) {
        result = false
      } else if (!objectTracker.destroyDataSet(currentDataset)) {
        result = false
      }
      currentDataset = null
    }

    return result

  }

  override fun doDeinitTrackers(): Boolean {
    TrackerManager.getInstance().deinitTracker(ObjectTracker.getClassType())
    return true
  }

  override fun onInitARDone(e: SampleApplicationException?) {
    if (e != null) {
      throw RuntimeException(e)
    }
    initApplicationAR()
    renderer?.setActive(true)
    glViewContainer.addView(glView)
    try {
      vuforiaAppSession.startAR(CAMERA_DIRECTION.CAMERA_DIRECTION_DEFAULT)
    } catch (e: SampleApplicationException) {
      Log.e("", e.string)
    }
    CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO)
    progress.visibility = View.GONE
  }

  override fun onVuforiaUpdate(state: State?) {
    if (needSwitch) {
      needSwitch = false
      val tm = TrackerManager.getInstance()
      val ot = tm.getTracker(ObjectTracker.getClassType()) as? ObjectTracker
      if (ot == null || currentDataset == null || ot.getActiveDataSet(0) == null) {
        Log.d("", "Failed to swap datasets")
        return
      }

      doUnloadTrackersData()
      doLoadTrackersData()
    }
  }
}