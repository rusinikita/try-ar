package com.nikita.tryar

import android.app.Activity
import android.os.Bundle
import com.nikita.tryar.ar.GLView

class MainActivity : Activity() {
  private lateinit var glView: GLView;

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    glView = findViewById(R.id.gl_view) as GLView
  }

  override fun onPause() {
    super.onPause()
    glView.onPause()
  }

  override fun onResume() {
    super.onResume()
    glView.onResume()
  }
}