package com.nikita.tryar

import android.app.Activity
import android.os.Bundle
import com.nikita.tryar.ar.ARDelegate
import com.nikita.tryar.ar.GLView
import io.reactivex.disposables.CompositeDisposable

class MainActivity : Activity() {
  private lateinit var glView: GLView
  private lateinit var arDelegate: ARDelegate
  private val disposable = CompositeDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

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