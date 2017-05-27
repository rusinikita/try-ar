package com.nikita.tryar

import android.app.Activity
import android.os.Bundle
import android.os.RemoteException
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.NestedScrollView
import android.util.Log
import android.view.WindowManager
import android.view.View
import android.widget.Toast
import com.nikita.tryar.ar.ARDelegate
import com.nikita.tryar.ar.GLView
import com.nikita.tryar.item_info.ItemInfoDelegate
import io.reactivex.disposables.CompositeDisposable
import org.altbeacon.beacon.BeaconConsumer
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.MonitorNotifier
import org.altbeacon.beacon.Region

class MainActivity : Activity(), BeaconConsumer {

  private lateinit var beaconManager: BeaconManager

  private lateinit var glView: GLView
  private lateinit var arDelegate: ARDelegate

  private lateinit var bottomSheet: NestedScrollView
  private lateinit var iteminfoDelegate: ItemInfoDelegate

  private lateinit var testButton: FloatingActionButton

  private val disposable = CompositeDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    beaconManager = BeaconManager.getInstanceForApplication(this)
    beaconManager.bind(this)

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
    beaconManager.unbind(this)
  }

  override fun onBeaconServiceConnect() {
    beaconManager.addMonitorNotifier(object : MonitorNotifier {
      override fun didDetermineStateForRegion(p0: Int, p1: Region?) {
        log("SWITCHED - $p0 - ${p1!!.bluetoothAddress}")
      }

      override fun didEnterRegion(p0: Region?) {
        log("Saw beacon")
      }

      override fun didExitRegion(p0: Region?) {
        log("Lost beacon")
      }
    })
    beaconManager.addRangeNotifier { beacons, region ->
      log("ranging")
      beacons.forEach {
        log("Distance - ${it.distance}, Bluetooth name - ${it.bluetoothName}")
      }
    }
    try {
      beaconManager.startMonitoringBeaconsInRegion(Region("monitoringid", null, null, null))
      beaconManager.startRangingBeaconsInRegion(Region("rangingid", null, null, null))
    } catch (e: RemoteException) {
      log(e.localizedMessage)
    }
  }

  private fun log(message: String) {
    Log.e("QQQ-QQQ", message)
    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
  }
}