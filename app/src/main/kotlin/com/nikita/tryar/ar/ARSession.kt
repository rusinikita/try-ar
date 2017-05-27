package com.nikita.tryar.ar

import android.app.Activity
import com.nikita.tryar.BuildConfig
import com.vuforia.INIT_FLAGS
import com.vuforia.Vuforia
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ARSession(val activity: Activity, val glView: GLView) {

  fun initAR(): Single<Unit> {
    return Single.fromCallable {
      Vuforia.setInitParameters(activity, INIT_FLAGS.GL_30, BuildConfig.VUFORIA_KEY)
      // Vuforia.init() blocks until an initialization step is
      // complete, then it proceeds to the next step and reports
      // progress in percents (0 ... 100%).
      // If Vuforia.init() returns -1, it indicates an error.
      // Initialization is done when progress has reached 100%.
      while (Vuforia.init() < 100) {
        /*NOP*/
      }
    }
      .doOnSuccess {
        // 1 TODO trackers initialization
        // 2 TODO textures, model loading
        // 3 TODO Renderer initialization
        // 4 TODO Camera initialization
      }
      .subscribeOn(Schedulers.computation())
      .observeOn(AndroidSchedulers.mainThread())
  }

  fun onDestroy() {

  }
}