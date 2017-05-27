package com.nikita.tryar

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.FloatingActionButton
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import com.nikita.tryar.ar.ARSession
import com.nikita.tryar.ar.GLView
import com.nikita.tryar.ar.MegaRenderer
import com.nikita.tryar.item_info.ItemInfoDelegate
import io.reactivex.disposables.CompositeDisposable

class MainActivity : Activity() {
  private lateinit var glView: GLView
  private lateinit var arSession: ARSession

  private lateinit var bottomSheet: LinearLayout
  private lateinit var iteminfoDelegate: ItemInfoDelegate

  private lateinit var testButton: FloatingActionButton

  private val disposable = CompositeDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    bottomSheet = findViewById(R.id.bottom_sheet) as LinearLayout
    val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    iteminfoDelegate = ItemInfoDelegate(bottomSheet, bottomSheetBehavior)

    testButton = findViewById(R.id.testButton) as FloatingActionButton
    testButton.setOnClickListener {
      Events.recognitions.onNext("test")
    }

    glView = findViewById(R.id.gl_view) as GLView
    glView.setRenderer(MegaRenderer(emptyList()))
    arSession = ARSession(this, glView)
    setupWindow()
    startARInitialization();
  }

  private fun setupWindow() {
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    window.setFlags(
      WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
      WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
  }

  private fun startARInitialization() {
    /*
    arSession.initAR().subscribe({ _, error ->
      error?.let { Snackbar.make(glView, "AR init failed, try to restart", Snackbar.LENGTH_INDEFINITE).show() }
    })
    */
  }

  override fun onPause() {
    super.onPause()
    glView.onPause()
  }

  override fun onResume() {
    super.onResume()
    glView.onResume()
  }

  override fun onDestroy() {
    super.onDestroy()
    arSession.onDestroy()
  }
}