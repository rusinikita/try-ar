package com.nikita.tryar

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import com.nikita.tryar.ar.ARDelegate
import com.nikita.tryar.ar.GLView
import com.nikita.tryar.ar.MegaRenderer
import io.reactivex.disposables.CompositeDisposable

class MainActivity : Activity() {
  private lateinit var glView: GLView
  private lateinit var arDelegate: ARDelegate
  private val disposable = CompositeDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    glView = findViewById(R.id.gl_view) as GLView
    glView.setRenderer(MegaRenderer(emptyList()))
    arDelegate = ARDelegate(this, glView)
    setupWindow()
    startARInitialization()
  }

  private fun setupWindow() {
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    window.setFlags(
      WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
      WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
  }

  private fun startARInitialization() {
    /*
    arDelegate.initAR().subscribe({ _, error ->
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
    arDelegate.onDestroy()
  }
}