package com.nikita.tryar

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.NestedScrollView
import android.view.WindowManager
import android.view.View
import com.nikita.tryar.ar.ARDelegate
import com.nikita.tryar.ar.GLView
import com.nikita.tryar.item_info.ItemInfoDelegate
import io.reactivex.disposables.CompositeDisposable

class MainActivity : Activity() {
  private lateinit var glView: GLView
  private lateinit var arDelegate: ARDelegate

  private lateinit var bottomSheet: NestedScrollView
  private lateinit var iteminfoDelegate: ItemInfoDelegate

  private lateinit var testButton: FloatingActionButton

  private val disposable = CompositeDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    bottomSheet = findViewById(R.id.bottom_sheet) as NestedScrollView
    val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
    iteminfoDelegate = ItemInfoDelegate(bottomSheet, bottomSheetBehavior, this)
    iteminfoDelegate.init()

    testButton = findViewById(R.id.testButton) as FloatingActionButton
    testButton.setOnClickListener {
      Events.recognitions.onNext("cheer")
    }

    glView = findViewById(R.id.gl_view) as GLView
    arDelegate = ARDelegate(this, glView)
    arDelegate.onCreate()
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