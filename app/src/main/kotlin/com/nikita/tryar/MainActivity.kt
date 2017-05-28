package com.nikita.tryar

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.NestedScrollView
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.nikita.tryar.ar.ARDelegate
import com.nikita.tryar.item_info.ItemInfoDelegate
import io.reactivex.disposables.CompositeDisposable

class MainActivity : Activity() {
  private lateinit var glViewContainer: ViewGroup
  private lateinit var progress: View
  private lateinit var arDelegate: ARDelegate

  private lateinit var bottomSheet: NestedScrollView
  private lateinit var iteminfoDelegate: ItemInfoDelegate

  private lateinit var switchButton: FloatingActionButton

  private val disposable = CompositeDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    bottomSheet = findViewById(R.id.bottom_sheet) as NestedScrollView
    val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
    iteminfoDelegate = ItemInfoDelegate(bottomSheet, bottomSheetBehavior, this)
    iteminfoDelegate.init()

    switchButton = findViewById(R.id.testButton) as FloatingActionButton

    glViewContainer = findViewById(R.id.gl_view_container) as ViewGroup
    progress = findViewById(R.id.progress_bar) as View
    arDelegate = ARDelegate(this, glViewContainer, progress, switchButton)
    arDelegate.onCreate()
    setupWindow()
  }

  private fun setupWindow() {
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    window.setFlags(
      WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
      WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
  }

  override fun onPause() {
    super.onPause()
    arDelegate.onPause()
  }

  override fun onResume() {
    super.onResume()
    arDelegate.onResume()
  }

  override fun onDestroy() {
    super.onDestroy()
    arDelegate.onDestroy()
  }
}